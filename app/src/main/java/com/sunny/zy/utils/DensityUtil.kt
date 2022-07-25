package com.sunny.zy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
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
    fun px2dp(px: Float): Int {
        return (px2dpFloat(px) + 0.5f).toInt()
    }

    fun dp2px(dp: Float): Int {
        return (dp2pxFloat(dp) + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return (sp2pxFloat(spValue) + 0.5f).toInt()
    }

    fun px2dpFloat(px: Float): Float {
        val density = ZyFrameStore.getContext().resources.displayMetrics.density
        return px / density
    }

    fun dp2pxFloat(dp: Float): Float {
        val density = ZyFrameStore.getContext().resources.displayMetrics.density
        return dp * density
    }

    fun sp2pxFloat(sp: Float): Float {
        val density = ZyFrameStore.getContext().resources.displayMetrics.scaledDensity
        return sp * density
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
                android.R.attr.actionBarSize, tv, true
            )
        ) {
            TypedValue.complexToDimensionPixelSize(
                tv.data, ZyFrameStore.getContext().resources.displayMetrics
            )
        } else 0
    }


    /**
     * 检查是否存在虚拟按键栏
     */
    @SuppressLint("PrivateApi")
    fun hasNavBar(): Boolean {
        val res: Resources = ZyFrameStore.getContext().resources
        val resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (resourceId != 0) {
            var hasNav = res.getBoolean(resourceId)

            var sNavBarOverride: String? = null

            try {
                val c = Class.forName("android.os.SystemProperties")
                val m = c.getDeclaredMethod("get", String::class.java)
                m.isAccessible = true
                sNavBarOverride = m.invoke(null, "qemu.hw.mainkeys") as String
            } catch (e: Throwable) {
            }

            if ("1" == sNavBarOverride) {
                hasNav = false
            } else if ("0" == sNavBarOverride) {
                hasNav = true
            }
            hasNav
        } else { // fallback
            !ViewConfiguration.get(ZyFrameStore.getContext()).hasPermanentMenuKey()
            false
        }
    }


    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     */
    fun getNavBarHeight(): Int {
        var dpi = 0
        val windowManager = ZyFrameStore.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        val c: Class<*>
        try {
            c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            dpi = dm.heightPixels - screenHeight()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dpi
    }
}