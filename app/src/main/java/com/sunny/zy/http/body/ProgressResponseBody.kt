package com.sunny.zy.http.body

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

class ProgressResponseBody(
    var responseBody: ResponseBody?,
    var progressListener: ProgressResponseListener
) : ResponseBody() {

    //包装完成的BufferedSource
    var bufferedSource: BufferedSource? = null


    override fun contentLength() = responseBody?.contentLength() ?: 0

    override fun contentType(): MediaType? = responseBody?.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody?.source())?.buffer()
        }
        return bufferedSource!!
    }


    private fun source(source: Source?): Source? {
        source?.let {
            return object : ForwardingSource(it) {
                //当前读取字节数
                var totalBytesRead = 0L
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    //回调，如果contentLength()不知道长度，会返回-1
                    progressListener.onResponseProgress(
                        totalBytesRead,
                        responseBody?.contentLength() ?: 0,
                        bytesRead == -1L
                    )
                    return bytesRead
                }
            }
        }
        return null
    }


    interface ProgressResponseListener {
        fun onResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean)
    }
}