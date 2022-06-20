package com.sunny.zy.widget

import android.content.Context
import android.view.View
import com.sunny.zy.R
import com.sunny.zy.base.ICreateStateView

/**
 * Desc
 * Author ZY
 * Date 2022/6/20
 */
open class DefaultCreateStateView : ICreateStateView {
    override var tvDescId: Int = R.id.tvDesc
    override var ivIconId = R.id.ivIcon

    override fun getLoadView(context: Context): View {
        return View.inflate(context, R.layout.zy_layout_loading, null)
    }

    override fun getErrorView(context: Context): View {
        return View.inflate(context, R.layout.zy_layout_error, null)
    }
}