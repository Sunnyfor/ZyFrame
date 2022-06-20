package com.sunny.zy.utils

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.base.bean.MenuBean
import com.sunny.zy.base.ZyToolBar


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/11/9 14:49
 */
class ToolbarUtil(var activity: BaseActivity) {

    var toolbar: ZyToolBar? = null

    private val menuList = ArrayList<MenuBean>()

    /**
     * 初始化Toolbar
     */
    fun initToolbar(rootLayout: ViewGroup, layoutRes: Int = 0) {
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )

        if (toolbar != null) {
            rootLayout.removeView(toolbar)
        }
        toolbar = ZyToolBar(rootLayout.context ?: return, layoutRes)
        rootLayout.addView(toolbar, 1, layoutParams)
        activity.setSupportActionBar(toolbar)
    }


    fun titleSimple(title: String, vararg menuItem: MenuBean) {
        show()
        menuList.clear()
        menuList.addAll(menuItem)
        toolbar?.visibility = View.VISIBLE
        toolbar?.title = title
        toolbar?.navigationIcon = null
        toolbar?.setNavigationOnClickListener(null)
    }


    fun titleDefault(title: String, vararg menuItem: MenuBean) {
        show()
        menuList.clear()
        menuList.addAll(menuItem)
        toolbar?.title = title
        toolbar?.setNavigationIcon(R.drawable.svg_title_back)
        toolbar?.setNavigationOnClickListener {
            activity.finish()
        }
    }

    fun setTitleCustom(vararg menuItem: MenuBean) {
        show()
        menuList.clear()
        menuList.addAll(menuItem)
    }


    fun hide() {
        toolbar?.visibility = View.GONE
    }

    fun show() {
        toolbar?.visibility = View.VISIBLE
    }

    fun createMenu() {
        toolbar?.createMenu(menuList)
    }
}