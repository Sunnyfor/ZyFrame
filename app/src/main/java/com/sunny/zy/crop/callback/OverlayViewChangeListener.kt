package com.sunny.zy.crop.callback

import android.graphics.RectF

interface OverlayViewChangeListener {
    fun onCropRectUpdated(cropRect: RectF)
}