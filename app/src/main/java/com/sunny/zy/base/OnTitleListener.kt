package com.sunny.zy.base

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/11/6 17:35
 */
interface OnTitleListener {

    fun titleDefault(title: String, vararg menuItem: BaseMenuBean)

    fun titleSimple(title: String, vararg menuItem: BaseMenuBean)

    fun titleSearch(title: String, vararg menuItem: BaseMenuBean)

    fun titleCenterDefault(title: String, vararg menuItem: BaseMenuBean)

    fun titleCenterSimple(title: String, vararg menuItem: BaseMenuBean)
}