package com.sunny.zy.base

/**
 * 枚举：错误View的类型
 * Created by Zy on 2018/8/2.
 */
data class PlaceholderBean(var viewType: Int) {
    val viewIdMap = hashMapOf<Int, Any>()

    companion object {
        const val loading = 10000 //加载View
        const val emptyData = 10001 //没有数据
        const val error = 10002 //发生错误
    }

}