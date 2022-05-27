package com.sunny.zy.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.sunny.zy.utils.DensityUtil
import com.sunny.zy.utils.LogUtil


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/11 15:44
 */
@Suppress("unused")
class CaptureButton : View {


    companion object {
        const val STATE_IDLE = 0x001 //空闲状态
        const val STATE_PRESS = 0x002 //按下状态
        const val STATE_LONG_PRESS = 0x003 //长按状态
        const val STATE_RECORDING = 0x004 //录制状态

        const val BUTTON_STATE_ONLY_CAPTURE = 0x101 //只能拍照
        const val BUTTON_STATE_ONLY_RECORDER = 0x102 //只能录像
        const val BUTTON_STATE_BOTH = 0x103 //两者都可以

    }


    private var state = 0 //当前按钮状态
    private var buttonState = 0//按钮可执行的功能状态（拍照,录制,两者）
    private val progressColor = -0x11e951ea //进度条颜色
    private val outsideColor = -0x11232324 //外圆背景色
    private val insideColor = -0x1 //内圆背景色
    private var eventY = 0f//Touch_Event_Down时候记录的Y值
    private var mPaint = Paint()
    private var strokeWidth = 0f //进度条宽度
    private var outsideAddSize = 0//长按外圆半径变大的Size
    private var insideReduceSize = 0 //长安内圆缩小的Size

    //中心坐标
    private var centerX = 0f
    private var centerY = 0f
    private var buttonRadius = 0f//按钮半径
    private var buttonOutsideRadius = 0f //外圆半径
    private var buttonInsideRadius = 0f//内圆半径
    private var buttonSize = 0//按钮大小
    private var progress = 0f//录制视频的进度
    private var duration = 0L//录制视频最大时间长度
    private var minDuration = 0//最短录制时间限制
    private var recordedTime = 0L//记录当前录制的时间

    private var rectF: RectF
    private var longPressRunnable: LongPressRunnable? = null//长按后处理的逻辑Runnable
    private var captureListener: CaptureListener? = null  //按钮回调接口
    private var timer: RecordCountDownTimer? = null //计时器

    constructor(context: Context) : this(context, null)


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        buttonSize = (DensityUtil.screenWidth() / 4.5).toInt()
        buttonRadius = buttonSize / 2.0f
        buttonOutsideRadius = buttonRadius
        buttonInsideRadius = buttonRadius * 0.75f
        strokeWidth = (buttonSize / 15).toFloat()
        outsideAddSize = buttonSize / 5
        insideReduceSize = buttonSize / 8
        mPaint.isAntiAlias = true
        progress = 0f
        longPressRunnable = LongPressRunnable()
        state = STATE_IDLE //初始化为空闲状态
        buttonState = BUTTON_STATE_BOTH //初始化按钮为可录制可拍照
        LogUtil.i("CaptureButton start")
        duration = 10 * 1000 //默认最长录制时间为10s
        LogUtil.i("CaptureButton end")
        minDuration = 1500 //默认最短录制时间为1.5s
        centerX = ((buttonSize + outsideAddSize * 2) / 2).toFloat()
        centerY = ((buttonSize + outsideAddSize * 2) / 2).toFloat()
        rectF = RectF(
            centerX - (buttonRadius + outsideAddSize - strokeWidth / 2),
            centerY - (buttonRadius + outsideAddSize - strokeWidth / 2),
            centerX + (buttonRadius + outsideAddSize - strokeWidth / 2),
            centerY + (buttonRadius + outsideAddSize - strokeWidth / 2)
        )
        timer = RecordCountDownTimer(duration, duration / 360L) //录制定时器
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize + outsideAddSize * 2, buttonSize + outsideAddSize * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.style = Paint.Style.FILL
        mPaint.color = outsideColor //外圆（半透明灰色）
        canvas.drawCircle(centerX, centerY, buttonOutsideRadius, mPaint)
        mPaint.color = insideColor //内圆（白色）
        canvas.drawCircle(centerX, centerY, buttonInsideRadius, mPaint)

        //如果状态为录制状态，则绘制录制进度条
        if (state == STATE_RECORDING) {
            mPaint.color = progressColor
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = strokeWidth
            canvas.drawArc(rectF, -90F, progress, false, mPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                LogUtil.i("state = $state")
                if (state == STATE_IDLE){
                    eventY = event.y //记录Y值
                    state = STATE_PRESS //修改当前状态为点击按下

                    //判断按钮状态是否为可录制状态
                    if (buttonState == BUTTON_STATE_ONLY_RECORDER || buttonState == BUTTON_STATE_BOTH) postDelayed(
                        longPressRunnable,
                        500
                    ) //同时延长500启动长按后处理的逻辑Runnable
                }
            }
            MotionEvent.ACTION_MOVE -> if (captureListener != null && state == STATE_RECORDING && (buttonState == BUTTON_STATE_ONLY_RECORDER || buttonState == BUTTON_STATE_BOTH)) {
                //记录当前Y值与按下时候Y值的差值，调用缩放回调接口
                captureListener?.recordZoom(eventY - event.y)
            }
            MotionEvent.ACTION_UP ->                 //根据当前按钮的状态进行相应的处理
                handlerUnPressByState()
        }
        return true
    }

    //当手指松开按钮时候处理的逻辑
    private fun handlerUnPressByState() {
        removeCallbacks(longPressRunnable) //移除长按逻辑的Runnable
        when (state) {
            STATE_PRESS -> if (captureListener != null && (buttonState == BUTTON_STATE_ONLY_CAPTURE || buttonState ==
                        BUTTON_STATE_BOTH)
            ) {
                startCaptureAnimation(buttonInsideRadius)
            } else {
                state = STATE_IDLE
            }
            STATE_RECORDING -> {
                timer!!.cancel() //停止计时器
                recordEnd() //录制结束
            }
        }
    }

    //录制结束
    private fun recordEnd() {
        if (captureListener != null) {
            if (recordedTime < minDuration) captureListener?.recordShort(recordedTime) //回调录制时间过短
            else captureListener?.recordEnd(recordedTime) //回调录制结束
        }
        resetRecordAnim() //重制按钮状态
    }

    //重制状态
    private fun resetRecordAnim() {
        state = STATE_IDLE
        progress = 0f //重制进度
        invalidate()
        //还原按钮初始状态动画
        startRecordAnimation(
            buttonOutsideRadius,
            buttonRadius,
            buttonInsideRadius,
            buttonRadius * 0.75f
        )
    }

    //内圆动画
    private fun startCaptureAnimation(inside_start: Float) {
        val insideAnim = ValueAnimator.ofFloat(inside_start, inside_start * 0.75f, inside_start)
        insideAnim.addUpdateListener { animation ->
            buttonInsideRadius = animation.animatedValue as Float
            invalidate()
        }
        insideAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                //回调拍照接口
                captureListener?.takePictures()
                state = STATE_IDLE
            }
        })
        insideAnim.duration = 100
        insideAnim.start()
    }

    //内外圆动画
    private fun startRecordAnimation(
        outside_start: Float,
        outside_end: Float,
        inside_start: Float,
        inside_end: Float
    ) {
        val outsideAnim = ValueAnimator.ofFloat(outside_start, outside_end)
        val insideAnim = ValueAnimator.ofFloat(inside_start, inside_end)
        //外圆动画监听
        outsideAnim.addUpdateListener { animation ->
            buttonOutsideRadius = animation.animatedValue as Float
            invalidate()
        }
        //内圆动画监听
        insideAnim.addUpdateListener { animation ->
            buttonInsideRadius = animation.animatedValue as Float
            invalidate()
        }
        val set = AnimatorSet()
        //当动画结束后启动录像Runnable并且回调录像开始接口
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                //设置为录制状态
                if (state == STATE_LONG_PRESS) {
                    if (captureListener != null) captureListener?.recordStart()
                    state = STATE_RECORDING
                    timer?.start()
                }
            }
        })
        set.playTogether(outsideAnim, insideAnim)
        set.duration = 100
        set.start()
    }

    //更新进度条
    private fun updateProgress(millisUntilFinished: Long) {
        recordedTime = (duration - millisUntilFinished)
        progress = 360f - millisUntilFinished / duration.toFloat() * 360f
        invalidate()
    }

    //录制视频计时器
    private inner class RecordCountDownTimer constructor(
        millisInFuture: Long,
        countDownInterval: Long
    ) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            updateProgress(millisUntilFinished)
        }

        override fun onFinish() {
            updateProgress(0)
            recordEnd()
        }
    }

    //长按线程
    private inner class LongPressRunnable : Runnable {
        override fun run() {
            state = STATE_LONG_PRESS //如果按下后经过500毫秒则会修改当前状态为长按状态
            //启动按钮动画，外圆变大，内圆缩小
            startRecordAnimation(
                buttonOutsideRadius,
                buttonOutsideRadius + outsideAddSize,
                buttonInsideRadius,
                buttonInsideRadius - insideReduceSize
            )
        }
    }

    /**************************************************
     * 对外提供的API*
     */
    //设置最长录制时间
    fun setDuration(duration: Long) {
        this.duration = duration
        timer = RecordCountDownTimer(duration, duration / 360) //录制定时器
    }

    //设置最短录制时间
    fun setMinDuration(duration: Int) {
        minDuration = duration
    }

    //设置回调接口
    fun setCaptureListener(captureListener: CaptureListener?) {
        this.captureListener = captureListener
    }

    //设置按钮功能（拍照和录像）
    fun setButtonFeatures(state: Int) {
        buttonState = state
    }

    //是否空闲状态
    fun isIdle(): Boolean {
        return state == STATE_IDLE
    }

    //设置状态
    fun resetState() {
        state = STATE_IDLE
    }


    interface CaptureListener {
        fun takePictures()

        fun recordShort(time: Long)

        fun recordStart()

        fun recordEnd(time: Long)

        fun recordZoom(zoom: Float)

        fun recordError()
    }
}