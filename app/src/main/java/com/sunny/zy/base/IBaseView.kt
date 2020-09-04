package com.sunny.zy.base

/**
 * 接口 IView基类
 * Created by Zy on 2018/8/2.
 */
interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun showError(errorType: ErrorViewType)

    fun hideError(errorType: ErrorViewType)

    fun showMessage(message: String)
}