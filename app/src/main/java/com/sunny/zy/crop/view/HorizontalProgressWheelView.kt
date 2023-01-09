package com.sunny.zy.crop.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.sunny.kit.utils.LogUtil
import com.sunny.zy.R

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
class HorizontalProgressWheelView : View {
    private val mCanvasClipBounds = Rect()
    private var mScrollingListener: ScrollingListener? = null
    private var mLastTouchedPosition = 0f
    private var mProgressLinePaint: Paint
    private var mProgressMiddleLinePaint: Paint
    private var mProgressLineWidth = 0
    private var mProgressLineHeight = 0
    private var mProgressLineMargin = 0
    private var mScrollStarted = false
    private var mTotalScrollDistance = 0f
    private var mMiddleLineColor = 0


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mMiddleLineColor = ContextCompat.getColor(context, R.color.color_white)
        mProgressLineWidth = context.resources.getDimensionPixelSize(R.dimen.dp_2)
        mProgressLineHeight = context.resources.getDimensionPixelSize(R.dimen.dp_20)
        mProgressLineMargin = context.resources.getDimensionPixelSize(R.dimen.dp_10)
        mProgressLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressLinePaint.style = Paint.Style.STROKE
        mProgressLinePaint.strokeWidth = mProgressLineWidth.toFloat()
        mProgressLinePaint.color = ContextCompat.getColor(context, R.color.preview_front)
        mProgressMiddleLinePaint = Paint(mProgressLinePaint)
        mProgressMiddleLinePaint.color = mMiddleLineColor
        mProgressMiddleLinePaint.strokeCap = Paint.Cap.ROUND
        mProgressMiddleLinePaint.strokeWidth =
            context.resources.getDimensionPixelSize(R.dimen.dp_4).toFloat()
    }


    fun setScrollingListener(scrollingListener: ScrollingListener?) {
        mScrollingListener = scrollingListener
    }

    fun setMiddleLineColor(@ColorInt middleLineColor: Int) {
        mMiddleLineColor = middleLineColor
        mProgressMiddleLinePaint.color = mMiddleLineColor
        invalidate()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mLastTouchedPosition = event.x
            MotionEvent.ACTION_UP -> {
                if (mScrollingListener == null) {
                    performClick()
                } else {
                    mScrollStarted = false
                    mScrollingListener?.onScrollEnd()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val distance = event.x - mLastTouchedPosition
                if (distance != 0f) {
                    if (!mScrollStarted) {
                        mScrollStarted = true
                        mScrollingListener?.onScrollStart()
                    }
                    onScrollEvent(event, distance)
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.getClipBounds(mCanvasClipBounds)
        val linesCount = mCanvasClipBounds.width() / (mProgressLineWidth + mProgressLineMargin)
        val deltaX = mTotalScrollDistance % (mProgressLineMargin + mProgressLineWidth).toFloat()
       for (i in 0 until linesCount) {
            if (i < linesCount / 4) {
                mProgressLinePaint.alpha = (255 * (i / (linesCount / 4).toFloat())).toInt()
            } else if (i > linesCount * 3 / 4) {
                mProgressLinePaint.alpha =
                    (255 * ((linesCount - i) / (linesCount / 4).toFloat())).toInt()
            } else {
                mProgressLinePaint.alpha = 255
            }
            canvas.drawLine(
                -deltaX + mCanvasClipBounds.left + i * (mProgressLineWidth + mProgressLineMargin),
                mCanvasClipBounds.centerY() - mProgressLineHeight / 4.0f,
                -deltaX + mCanvasClipBounds.left + i * (mProgressLineWidth + mProgressLineMargin),
                mCanvasClipBounds.centerY() + mProgressLineHeight / 4.0f, mProgressLinePaint
            )
        }
        canvas.drawLine(
            mCanvasClipBounds.centerX().toFloat(),
            mCanvasClipBounds.centerY() - mProgressLineHeight / 2.0f,
            mCanvasClipBounds.centerX().toFloat(),
            mCanvasClipBounds.centerY() + mProgressLineHeight / 2.0f,
            mProgressMiddleLinePaint
        )
    }

    private fun onScrollEvent(event: MotionEvent, distance: Float) {
        mTotalScrollDistance -= distance

        LogUtil.i("滑动值:$mTotalScrollDistance")

        postInvalidate()
        mLastTouchedPosition = event.x
        mScrollingListener?.onScroll(-distance, mTotalScrollDistance)

    }


    interface ScrollingListener {
        fun onScrollStart()
        fun onScroll(delta: Float, totalDistance: Float)
        fun onScrollEnd()
    }
}