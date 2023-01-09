package com.sunny.zy.crop.util

object CubicEasing {

    fun easeOut(time: Float, start: Float, end: Float, duration: Float): Float {
        val mTime = (time / duration - 1.0f)
        return end * (mTime * mTime * mTime + 1.0f) + start
    }

    fun easeIn(time: Float, start: Float, end: Float, duration: Float): Float {
        val mTime = time / duration
        return end * mTime * mTime * mTime + start
    }

    fun easeInOut(time: Float, start: Float, end: Float, duration: Float): Float {
        var mTime = time / (duration / 2.0f)
        return if (mTime < 1.0f) {
            end / 2.0f * mTime * mTime * mTime + start
        } else {
            mTime -= 2.0f
            end / 2.0f * (mTime * mTime * mTime + 2.0f) + start
        }
    }
}