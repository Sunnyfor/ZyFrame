package com.sunny.zy.base

import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * Presenter基类
 * Created by Zy on 2018/8/2.
 */
abstract class BasePresenter<T : IBaseView>(var view: T?) : CoroutineScope by MainScope() {


    fun showError(viewGroup: ViewGroup? = null, placeholderBean: PlaceholderBean) {
        view?.showPlaceholder(viewGroup, placeholderBean)
    }

    fun hideError(overlayViewType: Int) {
        view?.hidePlaceholder(overlayViewType)
    }

    fun showMessage(message: String) {
        view?.showMessage(message)
    }

    fun showLoading() {
        view?.showLoading()
    }

    fun hideLoading() {
        view?.hideLoading()
    }
}