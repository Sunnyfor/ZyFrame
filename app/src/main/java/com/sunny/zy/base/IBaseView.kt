package com.sunny.zy.base

import android.view.ViewGroup

/**
 * 接口 IView基类
 * Created by Zy on 2018/8/2.
 */
interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun showPlaceholder(viewGroup: ViewGroup? = null, placeholderBean: PlaceholderBean)

    fun hidePlaceholder(overlayViewType: Int)

    fun showMessage(message: String)
}