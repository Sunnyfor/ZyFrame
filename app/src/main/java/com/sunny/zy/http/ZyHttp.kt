package com.sunny.zy.http

import com.sunny.zy.http.bean.HttpResultBean
import com.sunny.zy.http.body.ProgressResponseBody
import com.sunny.zy.http.parser.GSonResponseParser
import com.sunny.zy.http.parser.IResponseParser
import com.sunny.zy.http.request.ZyRequest
import com.sunny.zy.utils.ZyCookieJar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

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

    //结果解析器（默认为Gson）
    private var iResponseParser: IResponseParser = GSonResponseParser()


    /**
     * 初始化OKHttp
     */
    private fun <T> getOkHttpClient(httpResultBean: HttpResultBean<T>): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ZyConfig.headerInterceptor)
            .addNetworkInterceptor(
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Platform.get().log(message, Platform.WARN, null)
                    }
                }).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            .addNetworkInterceptor {

                httpResultBean.url = it.request().url.toString()
                val originalResponse = it.proceed(it.request())
                originalResponse.newBuilder().body(
                    ProgressResponseBody(
                        originalResponse.body,
                        object : ProgressResponseBody.ProgressResponseListener {

                            override fun onResponseProgress(
                                bytesRead: Long,
                                contentLength: Long,
                                done: Boolean
                            ) {
                                httpResultBean.contentLength = contentLength
                                httpResultBean.readLength = bytesRead
                                httpResultBean.done = done
                                httpResultBean.notifyData(httpResultBean)
                            }
                        }
                    )
                ).build()
            }
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .connectTimeout(10000L, TimeUnit.MILLISECONDS) //连接超时时间
            .readTimeout(10000L, TimeUnit.MILLISECONDS) //读取超时时间
            .cookieJar(ZyCookieJar())
            .build()
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
     * @param httpResultBean 包含解析结果的实体bean
     */
    private fun <T> execution(
        request: Request,
        httpResultBean: HttpResultBean<T>
    ) {

        try {
            //执行异步网络请求
            val response = getOkHttpClient(httpResultBean).newCall(request).execute()

            //获取HTTP状态码
            httpResultBean.httpCode = response.code
            //获取Response回执信息
            httpResultBean.msg = response.message


            //请求成功进行解析
            if (response.isSuccessful) {
                response.body?.let {
                    httpResultBean.bean = iResponseParser.parserResponse<T>(
                        it,
                        httpResultBean.typeToken,
                        httpResultBean.serializedName
                    )
                }
            }

        } catch (e: Exception) {
            //出现异常获取异常信息
            httpResultBean.exception = e
            httpResultBean.msg = e.message
            e.printStackTrace()
        }
    }
}