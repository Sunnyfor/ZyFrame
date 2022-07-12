package com.sunny.zy.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.sunny.http.ZyHttp
import com.sunny.zy.ZyFrameConfig
import com.sunny.zy.utils.FileUtil
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
        builder.setLogLevel(ZyFrameConfig.glideLogLevel)
        val cacheSize = 100 * 1000L * 1000L
        builder.setDiskCache(
            DiskLruCacheFactory(FileUtil.getFile("glide").path, cacheSize)
        )
    }

    class Factory(private val client: Call.Factory) : ModelLoaderFactory<GlideUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {
        }
    }
}