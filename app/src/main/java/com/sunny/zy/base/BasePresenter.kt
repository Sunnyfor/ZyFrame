package com.sunny.zy.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * Presenter父类
 * Created by zhangye on 2018/8/2.
 */
abstract class BasePresenter<T : IBaseView>(var view: T?) : CoroutineScope by MainScope() {


    fun showError(code: Int, message: String) {
        val errorViewType = ErrorViewType(code, message)
        when (code) {
            0 -> view?.showError(errorViewType)
        }

    }

    fun hideError() {
        view?.hideError(ErrorViewType(0, "message"))
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