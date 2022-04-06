package com.sunny.zy.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * Desc Presenter基类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/8/2.
 */
abstract class BasePresenter<T : IBaseView>(var view: T?) : CoroutineScope by MainScope() {

    fun cancel() {
        view = null
        cancel(null)
    }
}