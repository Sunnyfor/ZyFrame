package com.sunny.zy.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Preconditions
import com.sunny.kit.utils.LogUtil
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/9/29 10:40
 */
class OkHttpStreamFetcher(var client: Call.Factory, var url: GlideUrl) : DataFetcher<InputStream>, okhttp3.Callback {

    private var call: Call? = null
    private var responseBody: ResponseBody? = null
    private var stream: InputStream? = null
    private var callback: DataFetcher.DataCallback<in InputStream>? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val requestBuilder: Request.Builder = Request.Builder().url(url.toStringUrl())
        for ((key, value) in url.headers.entries) {
            requestBuilder.addHeader(key, value)
        }
        val request: Request = requestBuilder.build()
        this.callback = callback

        call = client.newCall(request)
        call?.enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        LogUtil.i("OkHttp failed to obtain result:${e.message}")
        callback?.onLoadFailed(e)
    }

    override fun onResponse(call: Call, response: Response) {
        responseBody = response.body
        if (response.isSuccessful) {
            val contentLength: Long = Preconditions.checkNotNull(responseBody).contentLength()
            stream = ContentLengthInputStream.obtain(responseBody!!.byteStream(), contentLength)
            callback?.onDataReady(stream)
        } else {
            callback?.onLoadFailed(HttpException(response.message, response.code))
        }
    }


    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun cleanup() {
        try {
            if (stream != null) {
                stream?.close()
            }
        } catch (e: IOException) {
            // Ignored
        }
        if (responseBody != null) {
            responseBody?.close()
        }
        callback = null
    }

    override fun cancel() {
        val local = call
        local?.cancel()
    }
}