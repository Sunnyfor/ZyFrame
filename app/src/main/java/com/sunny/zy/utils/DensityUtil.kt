package com.sunny.zy.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Desc 屏幕密度工具类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/2/13
 */
object DensityUtil {

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(pxValue: Float): Int {
        val scale: Float = ZyFrameStore.getContext().resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dp2px(dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpValue,
            ZyFrameStore.getContext().resources.displayMetrics
        ).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spValue,
            ZyFrameStore.getContext().resources.displayMetrics
        ).toInt()
    }

    fun dp2pxFloat(dpValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpValue,
            ZyFrameStore.getContext().resources.displayMetrics
        )
    }

    fun sp2pxFloat(spValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spValue,
            ZyFrameStore.getContext().resources.displayMetrics
        )
    }

    /**
     * 获取手机屏幕的高度
     */
    fun screenHeight(): Int {
        return ZyFrameStore.getContext().resources.displayMetrics.heightPixels
    }

    /**
     * 获取手机屏幕的宽度
     */
    fun screenWidth(): Int {
        return ZyFrameStore.getContext().resources.displayMetrics.widthPixels
    }

    /**
     * 获取控件的宽度
     *
     * @param view 要获取宽度的控件
     * @return 控件的宽度
     */
    fun viewWidth(view: View): Int {
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(w, h)
        return view.measuredWidth
    }

    /**
     * 获取控件的高度
     *
     * @param view 要获取高度的控件
     * @return 控件的高度
     */
    fun viewHeight(view: View): Int {
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(w, h)
        return view.measuredHeight
    }

    /**
     * 获取控件在窗体中的位置
     *
     * @param view 要获取位置的控件
     * @return 控件位置的数组
     */
    fun getViewLocationInWindow(view: View): IntArray {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location
    }

    /**
     * 获取控件在整个屏幕中的 位置
     *
     * @param view 要获取位置的控件
     * @return 控件位置的数组
     */
    fun getViewLocationInScreen(view: View): IntArray {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return location
    }

    /**
     * 获取设备的尺寸
     */
    fun getDeviceSize(): Double {
        val dm = ZyFrameStore.getContext().resources.displayMetrics
        val sqrt = sqrt(dm.widthPixels.toDouble().pow(2.0) + dm.heightPixels.toDouble().pow(2.0))
        return sqrt / (160 * dm.density)
    }

    fun getDisplayMetrics(): DisplayMetrics {
        requireNotNull(ZyFrameStore.getContext()) { "context can't null" }
        val windowManager =
            ZyFrameStore.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getWindowWidth() = getDisplayMetrics().widthPixels

    fun getWindowHeight() = getDisplayMetrics().heightPixels

    fun getStatusBarHeight(): Int {
        val resources = ZyFrameStore.getContext().resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            val height = resources.getDimensionPixelSize(resourceId)
            Log.v("dbw", "Status height:$height")
            return height
        }
        return 0
    }

    fun getToolBarHeight(): Int {
        val tv = TypedValue()
        return if (ZyFrameStore.getContext().theme.resolveAttribute(
                R.attr.actionBarSize, tv, true
            )
        ) {
            TypedValue.complexToDimensionPixelSize(
                tv.data, ZyFrameStore.getContext().resources.displayMetrics
            )
        } else 0
    }

    /**
     * 获取软键盘的高度
     */
    fun getSoftKeyBoardHeight(): Int {
        return screenHeight() * 2 / 5
    }

}