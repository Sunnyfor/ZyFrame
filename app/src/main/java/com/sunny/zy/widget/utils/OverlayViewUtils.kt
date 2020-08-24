package com.sunny.zy.widget.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.sunny.zy.R
import com.sunny.zy.base.ErrorViewType
import kotlinx.android.synthetic.main.zy_layout_error.view.*

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/4/27 21:09
 */
class OverlayViewUtils {


    private var viewStore = HashMap<Int, View>()


    //创建View并缓存
    private fun getView(context: Context, viewType: Int): View? {
        if (viewStore.containsKey(viewType)) {
            return viewStore[viewType]
        }

        val view = when (viewType) {
            ErrorViewType.loading -> View.inflate(context, R.layout.zy_layout_loading, null)
            else -> View.inflate(context, R.layout.zy_layout_error, null).apply {

                when (viewType) {
                    ErrorViewType.networkError -> {
                        tvDesc.text = "连接服务器异常！"
                    }
                    ErrorViewType.emptyData -> {
                        tvDesc.text = "暂时没有数据！"
                    }
                }

            }
        }
        view?.let {
//            it.setOnClickListener { }
            viewStore.put(viewType, view)
        }
        return view
    }

    /**
     * 操作View的显示与关闭
     * @param viewGroup view的容器
     * @param viewType view的显示类型
     * @param isShow true 显示，false关闭
     */
    fun optionView(viewGroup: ViewGroup, viewType: Int, isShow: Boolean = true): View? {
        val view = getView(viewGroup.context, viewType)
        val isShowing = view?.tag ?: false

        if (isShow) {
            if (isShowing == false) {
                view?.tag = true
                viewGroup.addView(view)
            }
        } else {
            if (isShowing == true) {
                view?.tag = false
                viewGroup.removeView(view)
            }
        }
        return view
    }

    /**
     * 显示View
     * @param viewGroup view的容器
     * @param viewType view的显示类型
     */
    fun showView(viewGroup: ViewGroup, viewType: Int) {
        optionView(viewGroup, viewType, true)
    }

    /**
     * 关闭View
     * @param viewGroup view的容器
     * @param viewType view的显示类型
     */
    fun hideView(viewGroup: ViewGroup, viewType: Int) {
        optionView(viewGroup, viewType, false)
    }

}