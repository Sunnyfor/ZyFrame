package com.sunny.zy.http.response

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import okhttp3.Request

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/8/10 10:32
 */
interface IHttpExecute {
    fun executeDownload(request: Request, resultBean: DownLoadResultBean)
    fun <T> executeHttp(request: Request, resultBean: HttpResultBean<T>)
}