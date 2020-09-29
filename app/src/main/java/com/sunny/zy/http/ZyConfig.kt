package com.sunny.zy.http

import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.bean.BaseHttpResultBean
import com.sunny.zy.http.interceptor.HeaderInterceptor
import com.sunny.zy.http.parser.IResponseParser
import com.sunny.zy.http.parser.ZHResponseParser
import com.sunny.zy.http.request.ZyCookieJar
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Interceptor
import javax.net.ssl.HostnameVerifier

/**
 * 框架全局配置清单
 * Created by Zy on 2017/10/12.
 */
object ZyConfig {
    /**
     * 是否打印LOG
     */
    var isLog = true

    /**
     * IP地址
     */
    var IP = "127.0.0.1" // 内网测试地址

    /**
     * 端口
     */
    var PORT = "80"

    /**
     * 域名前缀：http? https
     */
    var HOST_PREFIX = "https"

    /**
     * 域名变量
     */
    val HOST: String
        get() {
            return "$HOST_PREFIX://$IP:$PORT"
        }


    var CONNECT_TIME_OUT = 10 * 1000L

    var READ_TIME_OUT = 10 * 1000L

    /**
     * provider权限
     */
    var authorities = "com.sunny.zy.provider"


    var TEMP = ZyFrameStore.getContext().getExternalFilesDir("temp")?.path ?: "" //内存卡缓存路径


    val headerInterceptor: HeaderInterceptor by lazy {
        HeaderInterceptor()
    }

    /**
     * 设置头信息
     */
    fun setHttpHeader(headerMap: HashMap<String, Any>) {
        headerInterceptor.setHttpHeader(headerMap)
    }


    /**
     * 数据结果解析器
     */
    var iResponseParser: IResponseParser = ZHResponseParser()


    /**
     * CookieJar配置
     */
    var zyCookieJar: ZyCookieJar = object : ZyCookieJar() {
        override fun setCookies(url: HttpUrl, cookies: List<Cookie>): List<Cookie>? = cookies
    }

    /**
     * url验证
     */
    var hostnameVerifier: HostnameVerifier = HostnameVerifier { _, _ -> true }


    /**
     * 网络请求全局拦截器
     */
    var networkInterceptor: Interceptor? = null


    /**
     * 网络请求全局回调
     */
    var httpResultCallback: ((resultBean: BaseHttpResultBean) -> Unit)? = null
}

