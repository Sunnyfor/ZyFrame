package com.sunny.zy.http.bean

import kotlinx.coroutines.CoroutineScope
import okhttp3.Call

abstract class BaseHttpResultBean {

    var url = ""        //请求URL
    var resUrl = ""      //响应URL
    var httpCode = 0    //请求code
    var message = ""
    var call: Call? = null
    var scope: CoroutineScope? = null
    var exception: Exception? = null //网络请求异常信息

    fun httpIsSuccess(): Boolean {
        if (httpCode in 200..299) {
            return true
        }
        return false
    }

    override fun toString(): String {
        return "BaseHttpResultBean(url='$url', resUrl='$resUrl', httpCode=$httpCode, message='$message', exception=$exception)"
    }
}