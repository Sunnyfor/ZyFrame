package com.sunny.zy.http.interceptor

import com.sunny.zy.utils.LogUtil
import com.sunny.zy.utils.isProbablyUtf8
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/9/29 09:54
 */
class DefaultLogInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val connection = chain.connection()
        val startLogSb = StringBuilder()

        startLogSb.append("${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        startLogSb.append("\n")

        val requestHeaderSize = request.headers.size
        for (i in 0 until requestHeaderSize) {
            startLogSb.append(logHeader(request.headers, i))
            if (i < requestHeaderSize -1){
                startLogSb.append("\n")
            }
        }

        val params = request.tag().toString()
        if (params.isNotEmpty()) {
            startLogSb.append("\n")
            startLogSb.append("Params: $params")
        }
        LogUtil.w("发起请求", startLogSb.toString(),false)

        val endLogSb = StringBuilder()
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            endLogSb.append(e.message)
            LogUtil.w("请求结束", endLogSb.toString(),false)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        val contentLength = responseBody?.contentLength()
        endLogSb.append("${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)")
        endLogSb.append("\n")

        //头信息
        val responseHeaderSize = response.headers.size
        for (i in 0 until responseHeaderSize) {
            endLogSb.append(logHeader(response.headers, i))
            endLogSb.append("\n")
        }

        if (response.promisesBody()) {
            responseBody?.source()?.let {
                it.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = it.buffer
                if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }
                val contentType = responseBody.contentType()
                val charset: Charset =
                    contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                if (buffer.isProbablyUtf8() && contentLength != 0L) {
                    val result = buffer.clone().readString(charset)
                    if (result.isNotEmpty()){

                        endLogSb.append("\n")
                        endLogSb.append(result.trim())
                    }
                }
            }
        }
        LogUtil.w("请求结束", endLogSb.toString(),false)
        return response
    }

    private fun logHeader(headers: Headers, i: Int): String {
        val value = headers.value(i)
        return headers.name(i) + ": " + value
    }

}