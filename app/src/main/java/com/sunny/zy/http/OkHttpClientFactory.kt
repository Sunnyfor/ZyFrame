package com.sunny.zy.http

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.interceptor.ZyHttpLoggingInterceptor
import com.sunny.zy.http.interceptor.ZyNetworkInterceptor
import com.sunny.zy.utils.LogUtil
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Desc
 * Author Zy
 * Date 2020/8/24 17:06
 */
class OkHttpClientFactory {

    private fun getBuild(): OkHttpClient.Builder {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(ZyConfig.headerInterceptor)
                .addNetworkInterceptor(ZyHttpLoggingInterceptor())
                .sslSocketFactory(
                    ZySSLSocketClient.createSSLSocketFactory(),
                    ZySSLSocketClient.getTrustManager()
                )
                .hostnameVerifier(ZyConfig.hostnameVerifier)
                .connectTimeout(ZyConfig.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS) //连接超时时间
                .readTimeout(ZyConfig.READ_TIME_OUT, TimeUnit.MILLISECONDS) //读取超时时间
                .cookieJar(ZyConfig.zyCookieJar)

        ZyConfig.networkInterceptor?.let {
            builder.addInterceptor(it)
        }
        return builder
    }

    /**
     * 创建新的Client对象
     */
    fun getOkHttpClient(): OkHttpClient {
        return getBuild().build()
    }

    /**
     * 创建附带下载进度的okHttpClient
     */
    fun createDownloadClient(downLoadResultBean: DownLoadResultBean): OkHttpClient {
        return getBuild().addNetworkInterceptor(ZyNetworkInterceptor(downLoadResultBean)).build()
    }
}
