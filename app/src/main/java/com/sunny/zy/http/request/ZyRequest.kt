package com.sunny.zy.http.request

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.http.ZyHttp
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Desc 生成网络请求
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29 12:22
 */
class ZyRequest {

    private fun getUrlSb(url: String) = StringBuilder().apply {
        if (!url.contains("://")) {
            append(ZyConfig.HOST)
            append("/")
        }
        append(url)
    }


    /**
     * GET请求生成
     */
    fun getRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        var paramsStr = ""
        if (params?.isNotEmpty() == true) {
            paramsStr = paramsToString(params)
            urlSb.append("?").append(paramsStr)
        }
        return Request.Builder().url(urlSb.toString()).tag(paramsStr).build()
    }

    fun headRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        var paramsStr = ""
        if (params?.isNotEmpty() == true) {
            paramsStr = paramsToString(params)
            urlSb.append("?").append(paramsStr)
        }
        return Request.Builder().url(urlSb.toString()).head().tag(paramsStr).build()
    }


    /**
     * POST-JSON请求生成
     */
    fun postJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).post(body).tag(json).build()
    }


    /**
     * POST-FORM请求生成
     */
    fun postFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        var paramsStr = ""
        if (params?.isNotEmpty() == true) {
            paramsStr = paramsToString(params)
            params.entries.forEach {
                body.add(it.key, it.value)
            }
        }
        return Request.Builder().url(urlSb.toString()).post(body.build()).tag(paramsStr).build()
    }


    /**
     * PUT-FORM请求生成
     */
    fun putFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        var paramsStr = ""
        if (params?.isNotEmpty() == true) {
            paramsStr = paramsToString(params)
            params.entries.forEach {
                body.add(it.key, it.value)
            }
        }
        return Request.Builder().url(urlSb.toString()).put(body.build()).tag(paramsStr).build()
    }

    /**
     * PUT-JSON请求生成
     */
    fun putJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).put(body).tag(json).build()
    }


    /**
     * PATCH-Form请求生成
     */
    fun patchFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        var paramsStr = ""
        if (params?.isNotEmpty() == true) {
            paramsStr = paramsToString(params)
            params.entries.forEach {
                body.add(it.key, it.value)
            }
        }
        return Request.Builder().url(urlSb.toString()).patch(body.build()).tag(paramsStr).build()
    }

    /**
     * PATCH-Jsonm请求生成
     */
    fun patchJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).patch(body).tag(json).build()
    }


    /**
     * DELETE-Form请求生成
     */
    fun deleteFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        var paramsStr = ""
        if (params?.isNotEmpty() == true) {
            paramsStr = paramsToString(params)
            params.entries.forEach {
                body.add(it.key, it.value)
            }
        }
        return Request.Builder().url(urlSb.toString()).delete(body.build()).tag(paramsStr).build()
    }

    /**
     *  DELETE-Json请求
     */
    fun deleteJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).delete(body).tag(json).build()
    }

    /**
     * FORM形式上传文件
     */
    fun formUploadRequest(url: String, params: Map<String, Any>): Request {
        val urlSb = getUrlSb(url)
        val path = params[ZyHttp.FilePath]
        var fileName: String? = params[ZyHttp.FileName]?.toString()

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)

        when (path) {
            is String -> {
                if (fileName == null) {
                    fileName = path.split("/").last()
                }
                body.addFormDataPart(
                    "file",
                    fileName,
                    File(path).asRequestBody("multipart/form-data".toMediaType())
                )
            }

            is File -> {
                if (fileName == null) {
                    fileName = path.name
                }
                body.addFormDataPart(
                    "file",
                    fileName,
                    path.asRequestBody("multipart/form-data".toMediaType())
                )
            }

            is Uri -> {
                if (fileName == null) {
                    fileName = DocumentFile.fromSingleUri(ZyFrameStore.getContext(), path)?.name
                }
                ZyFrameStore.getContext().contentResolver.openInputStream(path)?.use { stream ->
                    body.addFormDataPart(
                        "file",
                        fileName,
                        stream.readBytes().toRequestBody("multipart/form-data".toMediaType())
                    )
                }
            }
        }

        params.entries.forEach {
            if (it.key != ZyHttp.FilePath && it.key != ZyHttp.FileName) {
                body.addFormDataPart(
                    it.key,
                    null,
                    it.value.toString().toRequestBody("text/plain".toMediaType())
                )
            }
        }
        return Request.Builder().url(urlSb.toString()).post(body.build())
            .tag(paramsToString(params)).build()
    }

    private fun paramsToString(params: Map<String, Any>): String {
        val paramsSb = StringBuilder()
        params.entries.forEachIndexed { index, entry ->
            paramsSb.append(entry.key)
                .append("=")
                .append(entry.value)
            if (index < params.size - 1) {
                paramsSb.append("&")
            }
        }
        return paramsSb.toString()
    }
}