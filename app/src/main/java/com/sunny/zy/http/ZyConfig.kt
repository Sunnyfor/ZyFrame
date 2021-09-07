package com.sunny.zy.http

import android.util.Log
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.PlaceholderBean
import com.sunny.zy.http.bean.BaseHttpResultBean
import com.sunny.zy.http.interceptor.HeaderInterceptor
import com.sunny.zy.http.parser.IResponseParser
import com.sunny.zy.http.parser.ZHResponseParser
import com.sunny.zy.http.request.ZyCookieJar
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Interceptor
import java.util.regex.Pattern
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
     * 前缀：http? https
     */
    var HOST_PREFIX = "http"


    /**
     * 数据bean成功的code值
     */
    val baseModelSuccessCodes = arrayListOf("0","200")

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
                PORT = "80"
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


    var CONNECT_TIME_OUT = 10 * 1000L

    var READ_TIME_OUT = 10 * 1000L

    /**
     * provider权限
     */
    var authorities = "com.sunny.zy.provider"

    var click_interval = 500L

    var TEMP = ZyFrameStore.getContext().getExternalFilesDir("temp")?.path ?: "" //内存卡缓存路径

    /**
     * 设置StatusBar文字颜色
     */
    var statusBarIsDark = false

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


    /**
     * 设置Glide的日志级别
     */
    var glideLogLevel = Log.ERROR

    /**
     *  无数据展示布局
     */
    var emptyLayoutRes = R.layout.zy_layout_placeholder
    var emptyPlaceholderBean = PlaceholderBean(PlaceholderBean.emptyData).apply {
        viewIdMap[R.id.tv_desc] = R.string.emptyData
        viewIdMap[R.id.iv_icon] = R.drawable.svg_placeholder
    }

    /**
     *  发生错误展示布局
     */
    var errorLayoutRes = R.layout.zy_layout_placeholder
    var errorPlaceholderBean = PlaceholderBean(PlaceholderBean.error).apply {
        viewIdMap[R.id.tv_desc] = ""
        viewIdMap[R.id.iv_icon] = R.drawable.svg_placeholder
    }

    /**
     *  加载数据展示布局
     */
    var loadingLayoutRes = R.layout.zy_layout_loading
    var loadingPlaceholderBean = PlaceholderBean(PlaceholderBean.loading)
}

