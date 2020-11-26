package com.sunny.zy.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.sunny.zy.glide.OkHttpUrlLoader
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.http.ZyHttp
import okhttp3.Call
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    private val factory: Factory by lazy {
        Factory(ZyHttp.clientFactory.getOkHttpClient())
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            factory
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(ZyConfig.glideLogLevel)
    }

    class Factory(private val client: Call.Factory) : ModelLoaderFactory<GlideUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {

        }

    }
}