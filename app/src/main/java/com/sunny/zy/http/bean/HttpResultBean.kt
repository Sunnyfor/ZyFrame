package com.sunny.zy.http.bean

import com.alibaba.android.arouter.launcher.ARouter
import com.sunny.zy.utils.RouterPath
import com.sunny.zy.utils.ToastUtil

/**
 * Desc
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
 * Date 2020/4/29 12:37
 */
abstract class HttpResultBean<T>(
    var serializedName: String = "data"
) :
    BaseHttpResultBean<T>() {

    var msg: String? = "OK"  //请求结果
    var bean: T? = null //数据结果

    fun isSuccess(): Boolean {

        if (httpIsSuccess()) {
            if (exception == null) {
                return true
            } else {
                if (url.contains("login.html")) {
                    msg = "登录失效，请重新登录！"
                    //跳转登录页面
                    ARouter.getInstance().build(RouterPath.LOGIN_LOGIN_ACTIVITY)
                        .withBoolean("logout", true).navigation()
                }
            }
        }
        ToastUtil.show(msg)
        return false
    }

    override fun notifyData(baseHttpResultBean: BaseHttpResultBean<T>) {

    }

    override fun toString(): String {
        return "HttpResultBean(httpCode=$httpCode, msg='$msg', exception=$exception, bean=$bean)"
    }


}