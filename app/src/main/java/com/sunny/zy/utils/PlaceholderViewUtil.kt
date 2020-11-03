package com.sunny.zy.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sunny.zy.R
import com.sunny.zy.base.PlaceholderBean

/**
 * Desc
 * Author Zy
 * Date 2020/4/27 21:09
 */
class PlaceholderViewUtil {

    private var viewStore = HashMap<Int, View>()

    //创建View并缓存
    private fun getView(context: Context, viewType: Int): View? {
        if (viewStore.containsKey(viewType)) {
            return viewStore[viewType]
        }

        val view = when (viewType) {
            PlaceholderBean.loading -> View.inflate(context, R.layout.zy_layout_loading, null)
            else -> View.inflate(context, R.layout.zy_layout_placeholder, null)
        }
        view?.let {
            viewStore.put(viewType, view)
        }
        return view
    }

    /**
     * 显示View
     * @param viewGroup view的容器
     * @param placeholderBean view的数据
     */
    fun showView(
        viewGroup: ViewGroup,
        placeholderBean: PlaceholderBean
    ) {
        val view = getView(viewGroup.context, placeholderBean.viewType)
        view?.findViewById<TextView>(R.id.tv_desc)?.text = placeholderBean.text
        view?.findViewById<ImageView>(R.id.iv_icon)?.setImageResource(placeholderBean.icon)
        if (view?.parent == null) {
            viewGroup.addView(view)
        }
    }

    /**
     * 关闭View
     * @param viewGroup view的容器
     * @param viewType view的显示类型
     */
    fun hideView(viewGroup: ViewGroup, viewType: Int) {
        val view = getView(viewGroup.context, viewType)
        viewGroup.removeView(view)
    }
}