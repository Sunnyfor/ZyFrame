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
class SmoothScrollTimerTask(private val wheelView: WheelView, private val offset: Int) : TimerTask() {
    private var realTotalOffset = Int.MAX_VALUE
    private var realOffset = 0


    override fun run() {
        if (realTotalOffset == Int.MAX_VALUE) {
            realTotalOffset = offset
        }
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = (realTotalOffset.toFloat() * 0.1f).toInt()
        if (realOffset == 0) {
            realOffset = if (realTotalOffset < 0) {
                -1
            } else {
                1
            }
        }
        if (abs(realTotalOffset) <= 1) {
            wheelView.cancelFuture()
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
        } else {
            wheelView.totalScrollY = wheelView.totalScrollY + realOffset

            //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的 情况
            if (!wheelView.isLoop) {
                val itemHeight = wheelView.itemHeight
                val top = (-wheelView.initPosition).toFloat() * itemHeight
                val bottom = (wheelView.itemsCount - 1 - wheelView.initPosition).toFloat() * itemHeight
                if (wheelView.totalScrollY <= top || wheelView.totalScrollY >= bottom) {
                    wheelView.totalScrollY = wheelView.totalScrollY - realOffset
                    wheelView.cancelFuture()
                    wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
                    return
                }
            }
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
            realTotalOffset -= realOffset
        }
    }
}