package com.sunny.zy.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc Header拦截器
 * Author Zy
 * Date 2020.08.24
 */
class HeaderInterceptor : Interceptor {

    private var headerMap = hashMapOf<String, Any>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authorised = originalRequest.newBuilder()
        headerMap.forEach {
            authorised.header(it.key, it.value.toString())
        }
        return chain.proceed(authorised.build())
    }

    /**
     * 设置网络请求头信息
     */
    fun setHttpHeader(headerMap: HashMap<String, Any>) {
        this.headerMap.clear()
        this.headerMap.putAll(headerMap)
    }

}