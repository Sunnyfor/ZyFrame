package com.sunny.zy.widget.wheel

import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Desc Handler 消息类
 * Author ZY
 * Date 2022/5/14
 */
class MessageHandler(private val wheelView: WheelView) : Handler(Looper.getMainLooper()) {

    companion object {
        const val WHAT_INVALIDATE_LOOP_VIEW = 1000
        const val WHAT_SMOOTH_SCROLL = 2000
        const val WHAT_ITEM_SELECTED = 3000
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            WHAT_INVALIDATE_LOOP_VIEW -> wheelView.invalidate()
            WHAT_SMOOTH_SCROLL -> wheelView.smoothScroll(WheelView.ACTION.FLING)
            WHAT_ITEM_SELECTED -> wheelView.onItemSelected()
        }
    }
}