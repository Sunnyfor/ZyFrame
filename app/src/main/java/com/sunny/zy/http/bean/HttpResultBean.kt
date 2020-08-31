package com.sunny.zy.http.bean

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
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


    var bean: T? = null //数据结果


    fun isSuccess(): Boolean {
        if (httpIsSuccess()) {
            if (exception == null) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "${super.toString()} HttpResultBean(serializedName='$serializedName', typeToken=$typeToken, bean=$bean)"
    }


}