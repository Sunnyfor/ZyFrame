package com.sunny.zy.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sunny.zy.ZyFrameStore

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/11/12 14:53
 */
class BitmapUtil {
    private var resId = 0
    private var originalBitmap: Bitmap? = null

    fun getCroppedBitmap(res: Int, x: Int, y: Int, widget: Int, height: Int): Bitmap? {
        if (res != resId || originalBitmap == null) {
            destroy()
            resId = res
            originalBitmap = BitmapFactory.decodeResource(ZyFrameStore.getContext().resources, res)
        }

        var bitmapWidget = widget
        if (widget == 0) {
            bitmapWidget = originalBitmap?.width ?: 0
        }
        var bitmapHeight = height
        if (height == 0) {
            bitmapHeight = (originalBitmap?.height ?: 0) - y
        }

        return Bitmap.createBitmap(originalBitmap ?: return null, x, y, bitmapWidget, bitmapHeight)
    }

    fun destroy() {
        originalBitmap?.recycle()
        originalBitmap = null
    }
}