package com.sunny.zy.http.parser

import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import okhttp3.ResponseBody
import java.io.File

/**
 * Desc 数据解析
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29 14:47
 */
interface IResponseParser {

    fun <T> parserHttpResponse(responseBody: ResponseBody, httpResultBean: HttpResultBean<T>): T

    fun parserDownloadResponse(responseBody: ResponseBody, downLoadResultBean: DownLoadResultBean): File

}