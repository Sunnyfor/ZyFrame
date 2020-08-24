package com.sunny.zy.base

/**
 * 错误View的类型枚举
 * Created by zhangye on 2018/8/2.
 */
data class ErrorViewType(
    var errorCode: Int,
    var errorMessage: String,
    var errorIcon: Int = 0
) {
    companion object {
        const val loading = 10000 //加载View
        const val emptyData = 10001 //没有数据
        const val networkError = 10002 //网络错误的View
    }

}