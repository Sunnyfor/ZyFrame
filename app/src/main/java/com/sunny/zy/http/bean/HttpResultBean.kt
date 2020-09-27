package com.sunny.zy.http.bean

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc 请求结果实体类
 * Author Zy
 * Date 2020/4/29 12:37
 */
abstract class HttpResultBean<T>(
    var serializedName: String = "data"
) :
    BaseHttpResultBean() {

    var typeToken: Type

    init {
        val type = javaClass.genericSuperclass
        val args = (type as ParameterizedType).actualTypeArguments
        typeToken = args[0]
    }


    var bean: T? = null


    fun isSuccess(): Boolean {
        var isSuccess = false
        if (httpIsSuccess()) {
            //执行未出现异常
            if (exception == null) {
                isSuccess = true
            }
        }
        return isSuccess
    }

    override fun toString(): String {
        return "${super.toString()} HttpResultBean(serializedName='$serializedName', typeToken=$typeToken, bean=$bean)"
    }


}