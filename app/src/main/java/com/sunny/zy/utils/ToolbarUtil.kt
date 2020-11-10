package com.sunny.zy.utils

import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.base.BaseMenuBean
import com.sunny.zy.base.OnTitleListener
import com.sunny.zy.base.ZyToolBar


/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/11/9 14:49
 */
class ToolbarUtil(private var activity: BaseActivity?) {

    var menuList = ArrayList<BaseMenuBean>()

    var toolbar: ZyToolBar? = null

    private var viewMap = HashMap<ViewGroup, ZyToolBar?>()

    /**
     * 初始化Toolbar
     */
    fun initToolbar(rootView: ViewGroup, bodyView: View, isCustomToolbar: Boolean = false) {
        toolbar = viewMap[rootView]
        if (toolbar == null) {
            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            )
            toolbar = ZyToolBar(activity ?: return, isCustomToolbar)
            rootView.addView(toolbar, layoutParams)
            val styledAttributes: TypedArray =
                activity?.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                    ?: return
            styledAttributes.getDimension(0, 0f).toInt().let {
                styledAttributes.recycle()
                LogUtil.i("toolbar高度:$it")
                (bodyView.layoutParams as LayoutParams).topMargin = it
            }
            viewMap[rootView] = toolbar
        }
        activity?.setSupportActionBar(toolbar)
    }

    fun titleSimple(title: String, vararg menuItem: BaseMenuBean) {
        activity?.title = title
        toolbar?.navigationIcon = null
        toolbar?.setNavigationOnClickListener(null)
        addMenu(*menuItem)
    }


    fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        titleSimple(title, *menuItem)
        toolbar?.setNavigationIcon(R.drawable.svg_title_back)
        toolbar?.setNavigationOnClickListener {
            activity?.finish()
        }
    }


    /**
     * 保存toolbar菜单
     */
    private fun addMenu(vararg menuItem: BaseMenuBean) {
        menuList.clear()
        menuList.addAll(menuItem)
    }


    fun createMenu() {
        toolbar?.createMenu(menuList)
    }


    fun onDestroy(rootView: ViewGroup? = null) {
        if (rootView != null) {
            viewMap.remove(rootView)
        } else {
            viewMap.clear()
            activity = null
        }
    }

    /**
     * 清理Toolbar菜单
     */
    fun clearMenu(rootView: ViewGroup) {
        menuList.clear()
        viewMap[rootView]?.menu?.clear()
    }

}