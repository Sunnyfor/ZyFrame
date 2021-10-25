package com.sunny.zy.http.interceptor

import com.sunny.zy.utils.LogUtil
import com.sunny.zy.utils.isProbablyUtf8
import okhttp3.*
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/9/29 09:54
 */
class ZyHttpLoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val connection = chain.connection()
        val logSb = StringBuilder()

        logSb.append("${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        logSb.append("\n")


        for (i in 0 until request.headers.size) {
            logSb.append(logHeader(request.headers, i))
            logSb.append("\n")
        }

        val params = request.tag().toString()
        if (params.isNotEmpty()) {
            logSb.append("Params: $params")
        }

        LogUtil.w("发起请求", logSb.toString())
        logSb.clear()


        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logSb.append(e.message)
            LogUtil.w("请求结束", logSb.toString())
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        val contentLength = responseBody?.contentLength()
        logSb.append("${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)")
        logSb.append("\n")

        //头信息
        for (i in 0 until response.headers.size) {
            logSb.append(logHeader(response.headers, i))
            logSb.append("\n")
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
                    logSb.append(buffer.clone().readString(charset))
                }
            }
        }
        LogUtil.w("请求结束", logSb.toString())
        return response
    }

    private fun logHeader(headers: Headers, i: Int): String {
        val value = headers.value(i)
        return headers.name(i) + ": " + value
    }

}