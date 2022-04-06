package com.sunny.zy.base

import android.view.MenuItem

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/10/29 17:27
 */
class BaseMenuBean {

    companion object {
        //表示永远显示在菜单中
        const val NEVER = MenuItem.SHOW_AS_ACTION_NEVER

        //表示如果空间充足就显示在Toolbar，否则显示在菜单中
        const val IF_ROOM = MenuItem.SHOW_AS_ACTION_IF_ROOM

        //表示永远显示在Toolbar中，如果屏幕空间不够则不显示
        const val ALWAYS = MenuItem.SHOW_AS_ACTION_ALWAYS

        //这个值使菜单和它的图标，菜单文本一起显示
        const val WITH_TEXT = MenuItem.SHOW_AS_ACTION_WITH_TEXT
    }


    constructor(title: String?, onClickListener: () -> Unit) {
        this.title = title ?: ""
        this.onClickListener = onClickListener
    }


    constructor(icon: Int, onClickListener: () -> Unit) {
        this.icon = icon
        this.onClickListener = onClickListener
    }

    constructor(title: String?, icon: Int, onClickListener: () -> Unit) {
        this.title = title ?: ""
        this.icon = icon
        this.onClickListener = onClickListener
    }

    var title: String = ""
    var icon: Int = 0
    var showAsAction = ALWAYS
    var onClickListener: () -> Unit
}