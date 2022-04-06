package com.sunny.zy.utils

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.sunny.zy.ZyFrameStore

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/11/12 14:53
 */
class BitmapUtil {
    private var resId = 0
    private var originalBitmap: Bitmap? = null

    fun initBitmap(@DrawableRes res: Int, width: Int, height: Int) {
        destroy()
        resId = res
        val drawable = AppCompatResources.getDrawable(ZyFrameStore.getContext(), res)
        originalBitmap = drawable?.toBitmap(width, height)
    }

    fun getCroppedBitmap(x: Int, y: Int, width: Int, height: Int): Bitmap? {
        var bitmapWidget = width
        if (width == 0) {
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