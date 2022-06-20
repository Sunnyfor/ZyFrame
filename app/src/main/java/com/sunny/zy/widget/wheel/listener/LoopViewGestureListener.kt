package com.sunny.zy.widget.wheel.listener

import android.view.MotionEvent
import com.sunny.zy.widget.wheel.WheelView

/**
 * Desc 手势监听
 * Author ZY
 * Date 2022/5/14
 */
class LoopViewGestureListener(private val wheelView: WheelView) : android.view.GestureDetector.SimpleOnGestureListener() {

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        wheelView.scrollBy(velocityY)
        return true

    }
}