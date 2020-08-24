package com.sunny.zy.utils

import com.google.gson.reflect.TypeToken
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/6/12 11:44
 */
class ZyCookieJar : CookieJar {

    private val cookieStore = HashMap<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
        SpUtil.setObject(url.host, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {

        var list = cookieStore[url.host]

        if (list == null) {
            list = SpUtil.getObject(
                url.host,
                object : TypeToken<List<Cookie>>() {}.type
            ) ?: arrayListOf()
        }
        cookieStore[url.host] = list
        return list
    }
}