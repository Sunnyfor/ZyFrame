package com.sunny.zy.widget.wheel.timer

import com.sunny.zy.widget.wheel.MessageHandler
import com.sunny.zy.widget.wheel.WheelView
import java.util.*
import kotlin.math.abs


/**
 * Desc
 * Author ZY
 * Date 2022/5/14
 */
class InertiaTimerTask(private val wheelView: WheelView, velocityY: Float) : TimerTask() {

    private var mCurrentVelocityY: Float = Int.MAX_VALUE.toFloat() //当前滑动速度

    private val mFirstVelocityY: Float = velocityY//手指离开屏幕时的初始速度


    override fun run() {

        //防止闪动，对速度做一个限制。
        if (mCurrentVelocityY == Int.MAX_VALUE.toFloat()) {
            mCurrentVelocityY = if (abs(mFirstVelocityY) > 2000f) {
                if (mFirstVelocityY > 0) 2000f else -2000f
            } else {
                mFirstVelocityY
            }
        }

        //发送handler消息 处理平顺停止滚动逻辑
        if (abs(mCurrentVelocityY) in 0.0f..20f) {
            wheelView.cancelFuture()
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_SMOOTH_SCROLL)
            return
        }
        val dy = (mCurrentVelocityY / 100f).toInt()
        wheelView.totalScrollY = wheelView.totalScrollY - dy
        if (!wheelView.isLoop) {
            val itemHeight = wheelView.itemHeight
            var top = -wheelView.initPosition * itemHeight
            var bottom = (wheelView.itemsCount - 1 - wheelView.initPosition) * itemHeight
            if (wheelView.totalScrollY - itemHeight * 0.25 < top) {
                top = wheelView.totalScrollY + dy
            } else if (wheelView.totalScrollY + itemHeight * 0.25 > bottom) {
                bottom = wheelView.totalScrollY + dy
            }
            if (wheelView.totalScrollY <= top) {
                mCurrentVelocityY = 40f
                wheelView.totalScrollY = top
            } else if (wheelView.totalScrollY >= bottom) {
                wheelView.totalScrollY = bottom
                mCurrentVelocityY = -40f
            }
        }
        mCurrentVelocityY = if (mCurrentVelocityY < 0.0f) {
            mCurrentVelocityY + 20f
        } else {
            mCurrentVelocityY - 20f
        }

        //刷新UI
        wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
    }
}