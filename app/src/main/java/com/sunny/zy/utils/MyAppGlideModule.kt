package com.sunny.zy.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    private val  okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier(HostnameVerifier { _, _ -> true })
        .connectTimeout(10000L, TimeUnit.MILLISECONDS) //连接超时时间
        .readTimeout(10000L, TimeUnit.MILLISECONDS) //读取超时时间
        .cookieJar(ZyCookieJar())
        .build()

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )
    }
}