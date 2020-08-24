package com.sunny.zy.http.request

import com.sunny.zy.http.ZyConfig
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Desc
 * Author ZhangYe
 * Mail yongzuo.chen@foxmail.com
 * Date 2020/4/29 12:22
 */
class ZyRequest {


    private fun getUrlSb(url: String) = StringBuilder().apply {
        if (!url.contains("http://") && !url.contains("https://")) {
            append(ZyConfig.HOST)
            append("/")
        }
        append(url)
    }


    /**
     * GET请求生成
     */

    fun getRequest(url: String, params: HashMap<String, String>?): Request {
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
    fun postFormRequest(url: String, params: HashMap<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        params?.entries?.forEach {
            body.add(it.key, it.value)
        }
        return Request.Builder().url(urlSb.toString()).post(body.build()).build()
    }

    /**
     * PATCH-Form请求生成
     */
    fun patchFormRequest(url: String, params: HashMap<String, String>?): Request {
        val urlSb = getUrlSb(url)
        val body = FormBody.Builder()
        params?.entries?.forEach {
            body.add(it.key, it.value)
        }
        return Request.Builder().url(urlSb.toString()).patch(body.build()).build()
    }

    /**
     *  DELETE请求
     */
    fun deleteJsonRequest(url: String, json: String): Request {
        val urlSb = getUrlSb(url)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return Request.Builder().url(urlSb.toString()).delete(body).build()
    }

    /**
     * FORM形式上传文件
     */
    fun formUploadRequest(url: String, filePath: String): Request {
        val urlSb = getUrlSb(url)
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                filePath.split("/").last(),
                File(filePath).asRequestBody("multipart/form-data".toMediaType())
            )

        return Request.Builder().url(urlSb.toString()).post(body.build()).build()
    }
}