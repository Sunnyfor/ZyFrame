package com.sunny.zy.http

import com.sunny.zy.http.bean.HttpResultBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc ZyHttp封装层提供java使用
 * Author ZY
 * Date 2022/4/26
 */
class ZyHttpForJava(val scope: CoroutineScope) {

    fun <T> get(
        url: String,
        params: Map<String, String>? = null,
        httpResult: HttpResult<T>?
    ) {
        scope.launch {
            val httpResultBean = createHttpResultBean(httpResult)
            ZyHttp.get(url, params, httpResultBean)
            httpResult?.onResult(httpResultBean)
        }
    }

    fun <T> post(
        scope: CoroutineScope,
        url: String,
        params: Map<String, String>? = null,
        httpResult: HttpResult<T>?
    ) {
        scope.launch {
            val httpResultBean = createHttpResultBean(httpResult)
            ZyHttp.post(url, params, httpResultBean)
            httpResult?.onResult(httpResultBean)
        }
    }

    fun <T> postJosn(
        scope: CoroutineScope,
        url: String,
        params: String? = null,
        httpResult: HttpResult<T>?
    ) {
        scope.launch {
            val httpResultBean = createHttpResultBean(httpResult)
            ZyHttp.postJson(url, params ?: "", httpResultBean)
            httpResult?.onResult(httpResultBean)
        }
    }


    private fun <T> createHttpResultBean(httpResult: HttpResult<T>?): HttpResultBean<T> {
        val httpResultBean = object : HttpResultBean<T>() {}
        httpResult?.let {
            httpResultBean.type = it.type
        }
        return httpResultBean
    }
}

abstract class HttpResult<T> {
    lateinit var type: Type

    init {
        javaClass.genericSuperclass?.let {
            if (it is ParameterizedType) {
                type = it.actualTypeArguments[0]
            }
        }
    }

    abstract fun onResult(httpResultBean: HttpResultBean<T>)
}