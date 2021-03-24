package com.sunny.zy.base

/**
 * Desc 公共实体类
 * Author Zy
 * Date 2017/9/14
 */
open class BaseModel<T> {
    var msg: String = "success"
    var code: String = "0"

    var data: T? = null


    fun isSuccess(): Boolean {
        if (code == "0" && msg == "success") {
            return true
        }
        return false
    }

    override fun toString(): String {
        return "BaseModel(msg='$msg', code='$code', data=$data)"
    }

}