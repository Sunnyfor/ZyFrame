package com.sunny.zy.utils

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.sunny.zy.R
import com.sunny.zy.base.PlaceholderBean
import com.sunny.zy.ZyFrameConfig

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/27 21:09
 */
class PlaceholderViewUtil {

    private val showViewBeanList = arrayListOf<ViewBean>()

    data class ViewBean(private val viewGroup: ViewGroup, private var view: View) {

        fun addView(): Boolean {
            if (view.parent == null) {
                val layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                viewGroup.addView(view, layoutParams)
                return true
            }

            return false
        }

        fun removeView() {
            viewGroup.removeView(view)
        }

        fun setBackgroundResources(@DrawableRes res: Int) {
            view.setBackgroundResource(res)
        }

        fun getView(@IdRes id: Int): View = view.findViewById(id)
    }

    private var viewStore = HashMap<Int, ViewBean>()


    //创建View并缓存
    private fun createView(viewGroup: ViewGroup, viewType: Int): ViewBean? {
        val view = when (viewType) {
            PlaceholderBean.loading -> {
                View.inflate(viewGroup.context, ZyFrameConfig.loadingLayoutRes, null)
            }
            PlaceholderBean.error -> {
                View.inflate(viewGroup.context, ZyFrameConfig.errorLayoutRes, null)
            }
            PlaceholderBean.emptyData -> {
                View.inflate(viewGroup.context, ZyFrameConfig.emptyLayoutRes, null)
            }
            else -> {
                View.inflate(viewGroup.context, R.layout.zy_layout_placeholder, null)
            }
        }
        view.setOnClickListener { }
        val bean = ViewBean(viewGroup, view)
        viewStore[viewType] = bean
        return bean
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
        val viewBean: ViewBean?
        if (viewStore.containsKey(placeholderBean.viewType)) {
            viewBean = viewStore[placeholderBean.viewType]
        } else {
            viewBean = createView(viewGroup, placeholderBean.viewType)
            placeholderBean.viewIdMap.forEach {
                when (val itemView = viewBean?.getView(it.key)) {
                    is TextView -> {
                        if (it.value is Int) {
                            itemView.text = viewGroup.context.getString(it.value as Int)
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
        }

        viewBean?.let {
            it.addView()
            if (it.addView()) {
                showViewBeanList.add(it)
            }
        }

    }

    /**
     * 关闭View
     * @param viewType view的显示类型
     */
    fun hideView(viewType: Int) {
        viewStore[viewType]?.let {
            it.removeView()
            showViewBeanList.remove(it)
        }
    }


    /**
     * 修改背景颜色
     */
    fun setBackgroundResources(resInt: Int, viewType: Int) {
        viewStore[viewType]?.setBackgroundResources(resInt)
    }


    fun clear() {
        showViewBeanList.clear()
        viewStore.clear()
    }
}