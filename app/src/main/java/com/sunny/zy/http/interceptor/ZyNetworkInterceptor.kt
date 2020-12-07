package com.sunny.zy.http.interceptor

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.body.ProgressResponseBody
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
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
                        if (contentLength == 0L) {
                            return
                        }
                        downLoadResultBean.scope?.launch(Main) {
                            downLoadResultBean.contentLength = contentLength
                            downLoadResultBean.readLength = bytesRead
                            val progress = (bytesRead * 100L / contentLength).toInt() / 2
                            if (progress != downLoadResultBean.progress) {
                                downLoadResultBean.progress = progress
                                downLoadResultBean.notifyData(downLoadResultBean)
                            }
                        }
                    }
                }
            )
        ).build()
    }
}