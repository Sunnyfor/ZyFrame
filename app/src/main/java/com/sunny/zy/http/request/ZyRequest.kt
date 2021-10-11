package com.sunny.zy.http.request

import android.net.Uri
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
 * Author Zy
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
        if (params?.isNotEmpty() == true) {
            urlSb.append("?")
            params.entries.forEach { entry ->
                urlSb.append(entry.key)
                    .append("=")
                    .append(entry.value)
                    .append("&")
            }
            urlSb.deleteCharAt(urlSb.lastIndex)
        }

        return Request.Builder().url(urlSb.toString()).build()
    }

    fun headRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        if (params?.isNotEmpty() == true) {
            urlSb.append("?")
            params.entries.forEach { entry ->
                urlSb.append(entry.key)
                    .append("=")
                    .append(entry.value)
                    .append("&")
            }
            urlSb.deleteCharAt(urlSb.lastIndex)
        }
        return Request.Builder().url(urlSb.toString()).head().build()
    }


    /**
     * POST-JSON请求生成
     */
    fun postJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).post(body).build()
    }


    /**
     * POST-FORM请求生成
     */
    fun postFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        params?.entries?.forEach {
            body.add(it.key, it.value)
        }
        return Request.Builder().url(urlSb.toString()).post(body.build()).build()
    }


    /**
     * PUT-FORM请求生成
     */
    fun putFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        params?.entries?.forEach {
            body.add(it.key, it.value)
        }
        return Request.Builder().url(urlSb.toString()).put(body.build()).build()
    }

    /**
     * PUT-JSON请求生成
     */
    fun putJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).put(body).build()
    }


    /**
     * PATCH-Form请求生成
     */
    fun patchFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        params?.entries?.forEach {
            body.add(it.key, it.value)
        }
        return Request.Builder().url(urlSb.toString()).patch(body.build()).build()
    }

    /**
     * PATCH-Jsonm请求生成
     */
    fun patchJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).patch(body).build()
    }


    /**
     * DELETE-Form请求生成
     */
    fun deleteFormRequest(url: String, params: Map<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        params?.entries?.forEach {
            body.add(it.key, it.value)
        }
        return Request.Builder().url(urlSb.toString()).delete(body.build()).build()
    }

    /**
     *  DELETE-Json请求
     */
    fun deleteJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).delete(body).build()
    }

    /**
     * FORM形式上传文件
     */
    fun formUploadRequest(url: String, params: Map<String, Any>): Request {
        val urlSb = getUrlSb(url)
        val path = params[ZyHttp.FilePath]

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)

        when (path) {
            is String -> {
                body.addFormDataPart(
                    "file",
                    path.split("/").last(),
                    File(path).asRequestBody("multipart/form-data".toMediaType())
                )
            }

            is File -> {
                body.addFormDataPart(
                    "file",
                    path.name.split("/").last(),
                    path.asRequestBody("multipart/form-data".toMediaType())
                )
            }

            is Uri -> {
                ZyFrameStore.getContext().contentResolver.openInputStream(path)?.use { stream ->
                    body.addFormDataPart(
                        "file",
                        path.toString().split("/").last(),
                        stream.readBytes().toRequestBody("multipart/form-data".toMediaType())
                    )
                }
            }
        }

        params.entries.forEach {
            if (it.key != ZyHttp.FilePath) {
                body.addFormDataPart(it.key, it.value.toString())
            }
        }
        return Request.Builder().url(urlSb.toString()).post(body.build()).build()
    }
}