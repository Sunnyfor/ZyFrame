package com.sunny.zy.http.request

import com.google.gson.reflect.TypeToken
import com.sunny.zy.utils.SpUtil
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Desc Cookie持久化存储
 * Author Zy
 * Date 2020/6/12 11:44
 */
abstract class ZyCookieJar : CookieJar {

    private val cookieStore = HashMap<String, List<Cookie>>()

    abstract fun setCookies(url: HttpUrl, cookies: List<Cookie>): List<Cookie>?

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val list = setCookies(url, cookies) ?: cookies
        cookieStore[url.host] = list
        SpUtil.setObject(url.host, list)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {

        var list = cookieStore[url.host]

        if (list == null) {
            list = SpUtil.getObject(url.host, object : TypeToken<List<Cookie>>() {}.type) ?: arrayListOf()
        }
        cookieStore[url.host] = list
        return list
    }

}