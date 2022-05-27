package com.sunny.zy.http

import com.sunny.zy.http.bean.BaseHttpResultBean
import com.sunny.zy.http.interceptor.ZyHeaderInterceptor
import com.sunny.zy.http.parser.DefaultResponseParser
import com.sunny.zy.http.parser.IResponseParser
import com.sunny.zy.http.request.ZyCookieJar
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Interceptor
import java.util.regex.Pattern
import javax.net.ssl.HostnameVerifier

/**
 * Desc 网络请求配置清单
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2017/10/12.
 */
object ZyHttpConfig {

    /**
     * IP地址
     */
    var IP = "127.0.0.1" // 内网测试地址

    /**
     * 端口
     */
    var PORT = "80"

    /**
     * 前缀：http? https
     */
    var HOST_PREFIX = "http"

    /**
     * 后缀
     */
    var HOST_SPACE = ""

    /**
     * 域名变量
     */
    var HOST: String = ""
        set(value) {
            field = value
            val mValueSb = StringBuilder(value)
            val prefixPattern = "^(http|https|ftp)://"
            val prefix = Pattern.compile(prefixPattern).matcher(mValueSb)

            if (prefix.find()) {
                val group = prefix.group()
                HOST_PREFIX = group.replace("://", "")
                mValueSb.delete(mValueSb.indexOf(group), group.length)
            }

            val portPattern = ":\\d+"
            val port = Pattern.compile(portPattern).matcher(mValueSb)
            if (port.find()) {
                val group = port.group()
                val startIndex = mValueSb.indexOf(group) + group.length
                val endIndex = mValueSb.length
                HOST_SPACE = if (startIndex < endIndex) {
                    mValueSb.substring(startIndex, endIndex)
                } else {
                    ""
                }
                PORT = group.replace(":", "")
                mValueSb.delete(mValueSb.indexOf(group), mValueSb.length)
            } else {
                PORT = when (HOST_PREFIX) {
                    "ftp" -> "21"
                    "https" -> "443"
                    else -> "80"
                }
            }
            if (mValueSb.contains("/")) {
                val spaceSb = StringBuilder()
                val values = mValueSb.split("/")
                values.forEachIndexed { index, s ->
                    if (index == 0) {
                        IP = s
                    } else {
                        spaceSb.append("/")
                        spaceSb.append(s)
                    }
                }
                HOST_SPACE = spaceSb.toString()
            } else {
                IP = mValueSb.toString()
            }

        }
        get() {
            return "$HOST_PREFIX://$IP${if (PORT == "80") "" else ":$PORT"}$HOST_SPACE"
        }


    /**
     * 连接超时时间，单位毫秒
     */
    var CONNECT_TIME_OUT = 10 * 1000L

    /**
     * 读取超时时间，单位毫秒
     */
    var READ_TIME_OUT = 10 * 1000L

    /**
     * 数据bean成功的code值
     */
    val baseModelSuccessCodes = arrayListOf("0", "200")

    val headerInterceptor: ZyHeaderInterceptor by lazy {
        ZyHeaderInterceptor()
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
    var iResponseParser: IResponseParser = DefaultResponseParser()


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