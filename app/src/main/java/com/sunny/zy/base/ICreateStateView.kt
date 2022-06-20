package com.sunny.zy.base

import android.content.Context
import android.view.View

/**
 * Desc
 * Author ZY
 * Date 2022/6/20
 */
interface ICreateStateView {

    var tvDescId: Int

    var ivIconId: Int

    fun getLoadView(context: Context): View

    fun getErrorView(context: Context): View

}