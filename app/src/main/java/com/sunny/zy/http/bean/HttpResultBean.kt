package com.sunny.zy.http.bean

import com.sunny.zy.utils.ToastUtil
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc 请求结果实体类
 * Author Zy
 * Date 2020/4/29 12:37
 */
abstract class HttpResultBean<T>(
    var serializedName: String = "data"
) : BaseHttpResultBean() {

    lateinit var type: Type

    init {
        javaClass.genericSuperclass?.let {
            if (it is ParameterizedType) {
                type = it.actualTypeArguments[0]
            }
        }
    }

    var bean: T? = null

    fun isSuccess(): Boolean {
        if (httpIsSuccess() && message.isEmpty()) {
            return true
        }
        ToastUtil.show(message)
        return false
    }

    override fun toString(): String {
        return "${super.toString()} HttpResultBean(serializedName='$serializedName', typeToken=$type, bean=$bean)"
    }

}