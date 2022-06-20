package com.sunny.zy.base.widget.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.sunny.zy.R
import com.sunny.zy.ZyFrameConfig
import com.sunny.zy.base.IBaseView
import com.sunny.zy.base.bean.ErrorViewBean
import com.sunny.zy.widget.DefaultStateView

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2022/3/1 15:07
 */
abstract class BaseDialog(context: Context) : Dialog(context), IBaseView, View.OnClickListener {

    private val flParentView by lazy {
        FrameLayout(context)
    }

    open val defaultStateView: DefaultStateView by lazy {
        object : DefaultStateView(ZyFrameConfig.createStateView) {
            override fun getStateViewParent(): ViewGroup {
                return this@BaseDialog.getStateViewParent()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (val initRes = initLayout()) {
            is View -> {
                flParentView.addView(initRes)
            }
            is Int -> {
                flParentView.addView(LayoutInflater.from(context).inflate(initRes, flParentView, false))
            }
        }
        setContentView(flParentView)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
//        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawableResource(R.color.color_transparent)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        initView()
        loadData()
    }


    override fun showLoading() {
        defaultStateView.showLoading()
    }

    override fun hideLoading() {
        defaultStateView.hideLoading()
    }

    override fun showError(bean: ErrorViewBean) {
        defaultStateView.showError(bean)
    }

    override fun hideError() {
        defaultStateView.hideLoading()
    }


    override fun onClick(v: View) {
        onClickEvent(v)
    }

    override fun getStateViewParent(): ViewGroup {
        return flParentView
    }
}