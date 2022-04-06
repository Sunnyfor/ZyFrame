package com.sunny.zy.http.response

import com.sunny.zy.http.ZyConfig
import com.sunny.zy.http.ZyHttp
import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import okhttp3.Request
import org.json.JSONObject

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/8/10 10:36
 */
class ZHHttpExecute : IHttpExecute {
    override fun executeDownload(request: Request, resultBean: DownLoadResultBean) {
        resultBean.call = ZyHttp.clientFactory.createDownloadClient(resultBean).newCall(request)
        resultBean.call?.execute()?.let { response ->
            //获取HTTP状态码
            resultBean.httpCode = response.code
            //获取Response回执信息
            resultBean.message = response.message
            //获取请求URL
            resultBean.url = request.url.toString()
            response.body?.let {
                if (response.isSuccessful) {
                    resultBean.file =
                        ZyConfig.iResponseParser.parserDownloadResponse(it, resultBean)
                } else {
                    val msg = JSONObject(it.string()).optString("msg")
                    if (msg.isNotEmpty()) {
                        resultBean.message = msg
                    }
                }
            }

        }
    }

    override fun <T> executeHttp(request: Request, resultBean: HttpResultBean<T>) {
        resultBean.call = ZyHttp.clientFactory.getOkHttpClient().newCall(request)
        resultBean.call?.execute()?.let { response ->
            //获取HTTP状态码
            resultBean.httpCode = response.code
            //获取Response回执信息
            resultBean.message = response.message
            //获取响应URL
            resultBean.resUrl = response.request.url.toString()

            response.body?.let {
                if (response.isSuccessful) {
                    resultBean.bean = ZyConfig.iResponseParser.parserHttpResponse(
                        it, resultBean
                    )
                } else {
                    val msg = JSONObject(it.string()).optString("msg")
                    if (msg.isNotEmpty()) {
                        resultBean.message = msg
                    }
                }
            }
        }
    }
}