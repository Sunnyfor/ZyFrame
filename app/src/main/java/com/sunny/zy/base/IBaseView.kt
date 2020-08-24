package com.sunny.zy.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner


/**
 *
 * Created by zhangye on 2018/8/2.
 */
interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun showError(errorType: ErrorViewType)

    fun hideError(errorType: ErrorViewType)

    fun showMessage(message: String)
}