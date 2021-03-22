package com.sunny.zy.http.bean

import com.google.gson.reflect.TypeToken

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2021/3/19 10:04
 */


fun <T> getHttpResultBean(serializedName: String = "data"): HttpResultBean<T> {
    return object : HttpResultBean<T>(serializedName) {}.apply {
        typeToken =  object : TypeToken<T>() {}.type
    }
}

