package com.sunny.zy.utils

import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.base.BaseMenuBean
import com.sunny.zy.base.ZyToolBar


/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/11/9 14:49
 */
class ToolbarUtil(var activity: BaseActivity) {

    var toolbar: ZyToolBar? = null

    private val menuList = ArrayList<BaseMenuBean>()

    /**
     * 初始化Toolbar
     */
    fun initToolbar(fl_toolbar: ViewGroup, layoutRes: Int = 0) {
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )

        if (toolbar != null) {
            fl_toolbar.removeAllViews()
        }
        toolbar = ZyToolBar(fl_toolbar.context ?: return, layoutRes)
        fl_toolbar.addView(toolbar, layoutParams)
        activity.setSupportActionBar(toolbar)
    }

    fun titleSimple(title: String, vararg menuItem: BaseMenuBean) {
        menuList.clear()
        menuList.addAll(menuItem)
        toolbar?.title = title
        toolbar?.navigationIcon = null
        toolbar?.setNavigationOnClickListener(null)
    }


    fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        menuList.clear()
        menuList.addAll(menuItem)
        toolbar?.title = title
        toolbar?.setNavigationIcon(R.drawable.svg_title_back)
        toolbar?.setNavigationOnClickListener {
            activity.finish()
        }
    }

    fun createMenu() {
        toolbar?.createMenu(menuList)
    }
}