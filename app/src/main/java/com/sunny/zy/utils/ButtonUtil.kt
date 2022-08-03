package com.sunny.zy.utils

import android.view.View
import com.sunny.zy.R
import com.sunny.zy.ZyFrameConfig

/**
 * Desc 防止多次点击
 * Author ZY
 * Date 2022/8/3
 */
object ButtonUtil {

    /**
     * 点击事件处理
     */
    private var lastClickId = 0
    private var lastClickTime = 0L

    /**
     * 检测是否多次点击？true为多次点击
     */
    fun onClick(view: View): Boolean {
        val tag = view.getTag(R.id.clickInterval)

        if (tag == null) {
            view.setTag(R.id.clickInterval, ZyFrameConfig.clickInterval)
        }

        var interval = 0L
        if (tag is Int) {
            interval = tag.toLong()
        }

        if (tag is Long) {
            interval = tag
        }

        if (view.id == lastClickId && System.currentTimeMillis() - lastClickTime < interval) {
            return true
        }
        lastClickId = view.id
        lastClickTime = System.currentTimeMillis()
        return false

    }
}