package com.sunny.zy.utils

import android.content.res.TypedArray
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import com.sunny.zy.R
import com.sunny.zy.base.BaseMenuBean
import com.sunny.zy.base.ZyToolBar


/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/11/9 14:49
 */
class ToolbarUtil {

    var toolbar: ZyToolBar? = null

    /**
     * 初始化Toolbar
     */
    fun initToolbar(rootView: ViewGroup, bodyView: View, isCustomToolbar: Boolean = false) {
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )
        toolbar = ZyToolBar(rootView.context ?: return, isCustomToolbar)
        rootView.addView(toolbar, layoutParams)
        val styledAttributes: TypedArray =
            rootView.context?.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                ?: return
        styledAttributes.getDimension(0, 0f).toInt().let {
            styledAttributes.recycle()
            (bodyView.layoutParams as LayoutParams).topMargin = it
        }
    }

    fun titleSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbar?.title = title
        val menuList = ArrayList<BaseMenuBean>()
        menuList.addAll(menuItem)
        toolbar?.createMenu(menuList)
    }


    fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        titleSimple(title, *menuItem)
        toolbar?.setNavigationIcon(R.drawable.svg_title_back)
        toolbar?.setNavigationOnClickListener {
            Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_BACK)
        }
    }
}