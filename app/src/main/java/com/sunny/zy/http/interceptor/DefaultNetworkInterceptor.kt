package com.sunny.zy.http.interceptor

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.body.ProgressResponseBody
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Desc 用于拦截网络请进行数据的实施读取
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24 16:20
 */
class DefaultNetworkInterceptor(var downLoadResultBean: DownLoadResultBean) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        val body = ProgressResponseBody(
            originalResponse.body,
            object : ProgressResponseBody.ProgressResponseListener {
                override fun onResponseProgress(
                    bytesRead: Long,
                    contentLength: Long,
                    done: Boolean
                ) {
                    if (contentLength > 0L) {
                        downLoadResultBean.contentLength = contentLength
                        downLoadResultBean.readLength = bytesRead
                        val progress = (bytesRead * 100L / contentLength).toInt() / 2
                        downLoadResultBean.progress = progress
                        downLoadResultBean.scope?.launch(Main) {
                            downLoadResultBean.notifyData(downLoadResultBean)
                        }
                    }
                }
            }
        )
        return originalResponse.newBuilder().body(body).build()
    }
}