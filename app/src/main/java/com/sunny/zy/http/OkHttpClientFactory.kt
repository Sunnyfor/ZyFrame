package com.sunny.zy.http

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.interceptor.ZyHttpLoggingInterceptor
import com.sunny.zy.http.interceptor.ZyNetworkInterceptor
import com.sunny.zy.http.ssl.ZySSLSocketClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24 17:06
 */
class OkHttpClientFactory {

    private fun getBuild(): OkHttpClient.Builder {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(ZyHttpConfig.headerInterceptor)
                .addNetworkInterceptor(ZyHttpLoggingInterceptor())
                .sslSocketFactory(
                    ZySSLSocketClient.createSSLSocketFactory(),
                    ZySSLSocketClient.getTrustManager()
                )
                .hostnameVerifier(ZyHttpConfig.hostnameVerifier)
                .connectTimeout(ZyHttpConfig.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS) //连接超时时间
                .readTimeout(ZyHttpConfig.READ_TIME_OUT, TimeUnit.MILLISECONDS) //读取超时时间
                .cookieJar(ZyHttpConfig.zyCookieJar)
                .retryOnConnectionFailure(true)

        ZyHttpConfig.networkInterceptor?.let {
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
