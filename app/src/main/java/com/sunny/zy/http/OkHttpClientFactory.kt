package com.sunny.zy.http

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.interceptor.ZyNetworkInterceptor
import com.sunny.zy.http.request.ZyCookieJar
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * Desc
 * Author Zy
 * Date 2020/8/24 17:06
 */
class OkHttpClientFactory {

    private val defaultHttpClient: OkHttpClient by lazy {
        getBuild().build()
    }

    private fun getBuild(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(ZyConfig.headerInterceptor)
            .addNetworkInterceptor(
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Platform.get().log(message, Platform.WARN, null)
                    }
                }).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .connectTimeout(ZyConfig.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS) //连接超时时间
            .readTimeout(ZyConfig.READ_TIME_OUT, TimeUnit.MILLISECONDS) //读取超时时间
            .cookieJar(ZyConfig.zyCookieJar)
    }

    /**
     * 非下载请求复用Client对象
     */
    fun getOkHttpClient() = defaultHttpClient


    /**
     * 创建附带下载进度的okHttpClient
     */
    fun createDownloadClient(downLoadResultBean: DownLoadResultBean): OkHttpClient {
        return getBuild().addNetworkInterceptor(ZyNetworkInterceptor(downLoadResultBean)).build()
    }
}
