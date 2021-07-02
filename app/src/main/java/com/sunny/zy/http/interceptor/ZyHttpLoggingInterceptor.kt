package com.sunny.zy.http.interceptor

import com.sunny.zy.utils.LogUtil
import com.sunny.zy.utils.isProbablyUtf8
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
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

    private val divider = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";

    @Volatile
    private var headersToRedact = emptySet<String>()


    interface Logger {
        fun log(message: String)

        companion object {
            /** A [Logger] defaults output appropriate for the current platform. */
            @JvmField
            val DEFAULT: Logger = object : Logger {
                override fun log(message: String) {
                    Platform.get().log(message)
                }
            }
        }
    }

    fun redactHeader(name: String) {
        val newHeadersToRedact = TreeSet(String.CASE_INSENSITIVE_ORDER)
        newHeadersToRedact += headersToRedact
        newHeadersToRedact += name
        headersToRedact = newHeadersToRedact
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body
        val connection = chain.connection()
        val logSb = StringBuilder()
        logSb.append("发起请求").append("\n")
        logSb.append(divider).append("\n")
        logSb.append("${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        logSb.append("\n")
        var headers = request.headers
        if (requestBody != null) {
            // Request body headers are only present when installed as a network interceptor. When not
            // already present, force them to be included (if available) so their values are known.
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    logSb.append("Content-Type: $it")
                    logSb.append("\n")
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (headers["Content-Length"] == null) {
                    logSb.append("Content-Length: ${requestBody.contentLength()}")
                    logSb.append("\n")
                }
            }
        }

        for (i in 0 until headers.size) {
            logSb.append(logHeader(headers, i))
            logSb.append("\n")
        }

        if (requestBody == null) {
            logSb.append(request.method)
        } else if (bodyHasUnknownEncoding(request.headers)) {
            logSb.append("${request.method} (encoded body omitted)")
        } else if (requestBody.isDuplex()) {
            logSb.append("${request.method} (duplex request body omitted)")
        } else if (requestBody.isOneShot()) {
            logSb.append("${request.method} (one-shot body omitted)")
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val contentType = requestBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            if (buffer.isProbablyUtf8()) {
                logSb.append("Params: ${buffer.readString(charset)}")
                logSb.append("\n")
            }
        }
        LogUtil.w(logSb.toString())

        logSb.clear()

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logSb.append("请求结束").append("\n")
            logSb.append(divider).append("\n")
            logSb.append(e)
            LogUtil.w(logSb.toString())
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody?.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logSb.append("请求结束").append("\n")
        logSb.append(divider).append("\n")
        logSb.append("${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)")
        logSb.append("\n")

        headers = response.headers
        for (i in 0 until headers.size) {
            logSb.append(logHeader(headers, i))
            logSb.append("\n")
        }

        if (!response.promisesBody()) {
//                logger.log("<-- END HTTP")
        } else if (bodyHasUnknownEncoding(response.headers)) {
//                logger.log("<-- END HTTP (encoded body omitted)")
        } else {
            responseBody?.source()?.let {
                it.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = it.buffer
                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset =
                    contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                if (!buffer.isProbablyUtf8()) {
                    return response

                }

                if (contentLength != 0L) {
                      logSb.append(buffer.clone().readString(charset))
                }

                if (gzippedLength != null) {
//                        logger.log("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
                } else {
//                        logger.log("<-- END HTTP (${buffer.size}-byte body)")
                }
                LogUtil.w(logSb.toString())
            }
        }
        return response
    }

    private fun logHeader(headers: Headers, i: Int): String {
        val value = if (headers.name(i) in headersToRedact) "██" else headers.value(i)
        return headers.name(i) + ": " + value
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}