package com.sunny.zy.base

import android.view.View

/**
 * Desc 接口 IView基类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/8/2.
 */
interface IBaseView : IStateView {

    /**
     * 设置布局操作
     */
    fun initLayout(): Any?

    /**
     * 初始化View操作
     */
    fun initView()

    /**
     * 加载数据操作
     */
    fun loadData()

    /**
     * 点击事件回调
     */
    fun onClickEvent(view: View)


    fun onClose()
}