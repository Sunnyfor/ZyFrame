package com.sunny.zy.base

import com.sunny.zy.utils.ToastUtil

/**
 * 公共实体类
 * Created by 张野 on 2017/9/14.
 */

open class BaseModel<T> {
    var msg: String = "success"
    var code: String = "0"
    var data: T? = null


    fun isSuccess(): Boolean {
        if (msg == "success") {
            return true
        }
        ToastUtil.show(msg)
        return false
    }

    override fun toString(): String {
        return "BaseModel(msg='$msg', code='$code', data=$data)"
    }

}