package com.sunny.zy.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sunny.zy.R
import com.sunny.zy.base.PlaceholderBean
import com.sunny.zy.http.ZyConfig

/**
 * Desc
 * Author Zy
 * Date 2020/4/27 21:09
 */
class PlaceholderViewUtil {

    private var viewStore = HashMap<Int, View>()

    private val layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )


    //创建View并缓存
    private fun getView(context: Context, viewType: Int): View? {
        if (viewStore.containsKey(viewType)) {
            return viewStore[viewType]
        }
        val view = when (viewType) {
            PlaceholderBean.loading -> {
                View.inflate(context, ZyConfig.loadingLayoutRes, null)
            }
            PlaceholderBean.error -> {
                View.inflate(context, ZyConfig.errorLayoutRes, null)
            }
            PlaceholderBean.emptyData -> {
                View.inflate(context, ZyConfig.emptyLayoutRes, null)
            }
            else -> {
                View.inflate(context, R.layout.zy_layout_placeholder, null)
            }
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
        placeholderBean.viewIdMap.forEach {
            when (val itemView = view?.findViewById<View>(it.key)) {
                is TextView -> {
                    if (it.value is Int) {
                        itemView.text = view.context.getString(it.value as Int)
                    }
                    if (it.value is String) {
                        itemView.text = it.value.toString()
                    }
                }

                is ImageView -> {
                    if (it.value is Int) {
                        itemView.setImageResource(it.value as Int)
                    }
                }
            }
        }
        if (view?.parent == null) {
            viewGroup.addView(view, layoutParams)
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