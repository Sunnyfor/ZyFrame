package com.sunny.zy.base

import android.view.View

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/11/6 17:35
 */
interface OnTitleListener {

    fun showTitle()

    fun hideTitle()

    fun setTitleSimple(title: String, vararg menuItem: BaseMenuBean)

    fun setTitleCenterSimple(title: String, vararg menuItem: BaseMenuBean)

    fun setTitleDefault(title: String, vararg menuItem: BaseMenuBean)

    fun setTitleCenterDefault(title: String, vararg menuItem: BaseMenuBean)

    fun setTitleCustom(layoutRes: Int,vararg menuItem: BaseMenuBean)

    fun setTitleBackground(textColor: Int, backgroundColor: Int)

    fun setStatusBarColor(color: Int)

    fun setStatusBarDrawable(drawable: Int, relevantView: View? = null)

    fun setStatusBarTextModel(isDark: Boolean)

    fun showStatusBar(showText: Boolean? = true)

    fun hideStatusBar(showText: Boolean? = true)
}