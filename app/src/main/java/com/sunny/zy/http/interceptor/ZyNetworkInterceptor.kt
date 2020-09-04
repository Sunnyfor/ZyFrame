package com.sunny.zy.http.interceptor

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.body.ProgressResponseBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc 用于拦截网络请进行数据的实施读取
 * Author Zy
 * Date 2020/8/24 16:20
 */
class ZyNetworkInterceptor(var downLoadResultBean: DownLoadResultBean) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
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
                        downLoadResultBean.contentLength = contentLength
                        downLoadResultBean.readLength = bytesRead
                        downLoadResultBean.done = done
                        downLoadResultBean.notifyData(downLoadResultBean)
                    }
                }
            )
        ).build()
    }
}