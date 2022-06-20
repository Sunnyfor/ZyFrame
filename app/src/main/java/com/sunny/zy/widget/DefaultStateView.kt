package com.sunny.zy.widget

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sunny.zy.base.ICreateStateView
import com.sunny.zy.base.IStateView
import com.sunny.zy.base.bean.ErrorViewBean

/**
 * Desc
 * Author ZY
 * Date 2022/6/20
 */
abstract class DefaultStateView(var createStateView: ICreateStateView) : IStateView {

    private val loadingView by lazy {
        createStateView.getLoadView(getStateViewParent().context).apply {
            setOnClickListener { }
        }
    }

    private val errorView by lazy {
        createStateView.getErrorView(getStateViewParent().context).apply {
            setOnClickListener { }
        }
    }


    override fun showLoading() {
        if (loadingView.parent == null) {
            getStateViewParent().addView(loadingView)
        }
    }

    override fun hideLoading() {
        getStateViewParent().removeView(loadingView)
    }

    override fun showError(bean: ErrorViewBean) {
        hideLoading()
        errorView.findViewById<TextView>(createStateView.tvDescId)?.text = bean.desc
        val ivIcon = errorView.findViewById<ImageView>(createStateView.ivIconId)
        if (bean.isGif) {
            Glide.with(errorView.context)
                .asGif()
                .load(bean.resId)
                .into(ivIcon)
        } else {
            ivIcon?.setImageResource(bean.resId)
        }

        if (errorView.parent == null) {
            getStateViewParent().addView(errorView)
        }
    }

    override fun hideError() {
        getStateViewParent().removeView(errorView)
    }

}