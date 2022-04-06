package com.sunny.zy.base

import android.view.ViewGroup

/**
 * Desc 接口 IView基类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/8/2.
 */
interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun showPlaceholder(viewGroup: ViewGroup, placeholderBean: PlaceholderBean)

    fun hidePlaceholder(viewType: Int)

    fun showMessage(message: String)
}