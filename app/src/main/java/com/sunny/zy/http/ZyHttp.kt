package com.sunny.zy.http

import com.sunny.zy.http.bean.BaseHttpResultBean
import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import com.sunny.zy.http.request.ZyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/4/28 02:07
 */
@Suppress("UNCHECKED_CAST")
object ZyHttp {

    //请求创建器
    private var zyRequest = ZyRequest()

    private val clientFactory: OkHttpClientFactory by lazy {
        OkHttpClientFactory()
    }

    /**
     * get请求
     * @param url URL服务器地址
     * @param params 传递的数据map（key,value)
     * @param httpResultBean 包含解析结果的实体bean
     */
    suspend fun <T> get(
        url: String,
        params: HashMap<String, String>? = null,
        httpResultBean: HttpResultBean<T>
    ) {
        return withContext(Dispatchers.IO) {
            //创建okHttp请求
            val request = zyRequest.getRequest(url, params)
            execution(request, httpResultBean)
        }
    }


    /**
     * postForm请求
     * @param url URL服务器地址
     * @param params 传递的数据map（key,value)
     * @param httpResultBean 包含解析结果的实体bean
     */
    suspend fun <T> post(
        url: String,
        params: HashMap<String, String>?,
        httpResultBean: HttpResultBean<T>
    ) {
        return withContext(Dispatchers.IO) {
            //创建okHttp请求
            val request = zyRequest.postFormRequest(url, params)
            execution(request, httpResultBean)
        }
    }


    suspend fun <T> patch(
        url: String,
        params: HashMap<String, String>?,
        httpResultBean: HttpResultBean<T>
    ) {
        return withContext(Dispatchers.IO) {
            //创建okHttp请求
            val request = zyRequest.patchFormRequest(url, params)
            execution(request, httpResultBean)
        }
    }


    /**
     * post传递JSON请求
     * @param url URL服务器地址
     * @param json 传递的json字符串
     * @param httpResultBean 包含解析结果的实体bean
     */
    suspend fun <T> postJson(url: String, json: String, httpResultBean: HttpResultBean<T>) {
        return withContext(Dispatchers.IO) {
            //创建okHttp请求
            val request = zyRequest.postJsonRequest(url, json)
            execution(request, httpResultBean)
        }
    }


    suspend fun <T> deleteJson(url: String, json: String, httpResultBean: HttpResultBean<T>) {
        return withContext(Dispatchers.IO) {
            //创建okHttp请求
            val request = zyRequest.deleteJsonRequest(url, json)
            execution(request, httpResultBean)
        }

    }


    suspend fun <T> formUpload(url: String, filePath: String, httpResultBean: HttpResultBean<T>) {
        return withContext(Dispatchers.IO) {
            //创建okHttp请求
            val request = zyRequest.formUploadRequest(url, filePath)
            execution(request, httpResultBean)
        }
    }


    /**
     * 执行网络请求并处理结果
     * @param request OkHttp请求对象
     * @param baseHttpResultBean 包含解析结果的实体bean
     */
    private fun <T> execution(
        request: Request,
        baseHttpResultBean: BaseHttpResultBean<T>
    ) {

        try {

            //存储URL
            baseHttpResultBean.url = request.url.toString()
            //执行异步网络请求
            if (baseHttpResultBean is DownLoadResultBean) {
                executeDownload(request, baseHttpResultBean)
            } else if (baseHttpResultBean is HttpResultBean) {
                executeHttp(request, baseHttpResultBean)
            }

        } catch (e: Exception) {
            //出现异常获取异常信息
            baseHttpResultBean.exception = e
            baseHttpResultBean.message = e.message ?: ""
        }
    }

    private fun executeDownload(request: Request, resultBean: DownLoadResultBean) {
        val response = clientFactory.createDownloadClient(resultBean).newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.let {
                resultBean.file = ZyConfig.iResponseParser.parserDownloadResponse(it, resultBean)
            }
        }

        //获取HTTP状态码
        resultBean.httpCode = response.code
        //获取Response回执信息
        resultBean.message = response.message
    }


    private fun <T> executeHttp(request: Request, resultBean: HttpResultBean<T>) {
        val response = clientFactory.getOkHttpClient().newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.let {
                resultBean.bean = ZyConfig.iResponseParser.parserHttpResponse(
                    it, resultBean
                )
            }
        }
        //获取HTTP状态码
        resultBean.httpCode = response.code
        //获取Response回执信息
        resultBean.message = response.message
    }
}