package com.sunny.zy.http.base

import com.sunny.zy.base.BasePresenter
import com.sunny.zy.base.IBaseView
import com.sunny.zy.http.ZyHttpForJava


/**
 * Desc Model基础类, 供Java数据Model使用
 * Author ZY
 * Date 2022/4/29
 */
open class BaseModel<T : BasePresenter<V>, V : IBaseView>(private val presenter: T) {

    val zyHttpJava by lazy { ZyHttpForJava(presenter) }

    fun getPresenter(): T {
        return presenter
    }
}