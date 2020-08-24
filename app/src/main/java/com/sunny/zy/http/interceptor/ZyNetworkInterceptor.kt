package com.sunny.zy.http.interceptor

import com.sunny.zy.http.bean.HttpResultBean
import com.sunny.zy.http.body.ProgressResponseBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc 用于拦截网络请进行数据的实施读取
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/8/24 16:20
 */
class ZyNetworkInterceptor<T>(var httpResultBean: HttpResultBean<T>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        httpResultBean.url = chain.request().url.toString()
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder().body(
            ProgressResponseBody(
                originalResponse.body,
                object : ProgressResponseBody.ProgressResponseListener {
                    override fun onResponseProgress(
                        bytesRead: Long,
                        contentLength: Long,
                        done: Boolean
                    ) {
                        httpResultBean.contentLength = contentLength
                        httpResultBean.readLength = bytesRead
                        httpResultBean.done = done
                        httpResultBean.notifyData(httpResultBean)
                    }
                }
            )
        ).build()
    }
}