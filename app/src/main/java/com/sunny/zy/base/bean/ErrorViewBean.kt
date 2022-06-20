package com.sunny.zy.base.bean

import androidx.annotation.DrawableRes
import com.sunny.zy.R

/**
 * Desc 错误View的类型
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/8/2.
 */
data class ErrorViewBean(var desc: String = "", @DrawableRes var resId: Int = R.drawable.svg_placeholder) {
    var isGif = false
}