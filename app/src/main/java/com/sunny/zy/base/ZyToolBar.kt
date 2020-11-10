package com.sunny.zy.base

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.sunny.zy.R

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/11/6 09:48
 */
class ZyToolBar : Toolbar {

    private var isCustomToolbar = false

    var layoutRes = R.layout.zy_default_title

    private val titleView: View by lazy {
        LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    constructor(context: Context, isCustomToolbar: Boolean) : super(context) {
        this.isCustomToolbar = isCustomToolbar
        if (isCustomToolbar) {
            setContentInsetsAbsolute(0, 0)
            setContentInsetsRelative(0, 0)
            addView(titleView)
        }
        setBackgroundResource(R.color.colorPrimary)
        setTitleTextColor(ContextCompat.getColor(context,R.color.textColorPrimary))
        setSubtitleTextColor(ContextCompat.getColor(context,R.color.textColorPrimary))
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setTitle(resId: Int) {
        if (!isCustomToolbar) {
            super.setTitle(resId)
        } else {
            titleView.findViewById<TextView>(R.id.zy_tv_title).text = resources.getString(resId)
        }
    }

    override fun setTitle(title: CharSequence?) {
        if (!isCustomToolbar) {
            super.setTitle(title)
        } else {
            titleView.findViewById<TextView>(R.id.zy_tv_title).text = title
        }
    }

    override fun setTitleTextColor(color: ColorStateList) {
        if (!isCustomToolbar) {
            super.setTitleTextColor(color)
        } else {
            titleView.findViewById<TextView>(R.id.zy_tv_title).setTextColor(color)
        }
    }

    override fun setTitleTextColor(color: Int) {
        if (!isCustomToolbar) {
            super.setTitleTextColor(color)
        } else {
            titleView.findViewById<TextView>(R.id.zy_tv_title).setTextColor(color)
        }

    }

    override fun setNavigationIcon(resId: Int) {
        if (!isCustomToolbar) {
            super.setNavigationIcon(resId)
        } else {
            titleView.findViewById<AppCompatImageButton>(R.id.zy_ib_back).setImageResource(resId)
        }
    }

    override fun setNavigationIcon(icon: Drawable?) {
        if (!isCustomToolbar) {
            super.setNavigationIcon(icon)
        } else {
            titleView.findViewById<AppCompatImageButton>(R.id.zy_ib_back).setImageDrawable(icon)
        }
    }

    override fun setNavigationOnClickListener(listener: OnClickListener?) {

        if (!isCustomToolbar) {
            super.setNavigationOnClickListener(listener)
        } else {
            titleView.findViewById<AppCompatImageButton>(R.id.zy_ib_back)
                .setOnClickListener(listener)
        }
    }

    override fun inflateMenu(resId: Int) {
        if (!isCustomToolbar) {
            super.inflateMenu(resId)
        }
    }

    override fun getMenu(): Menu? {
        if (!isCustomToolbar) {
            return super.getMenu()
        }
        return titleView.findViewById<ActionMenuView>(R.id.zy_menu_view).menu
    }


    fun createMenu(menuItem: ArrayList<BaseMenuBean>) {
        menuItem.forEach { bean ->
            menu?.add(bean.title)?.let { menuItem ->
                menuItem.setOnMenuItemClickListener {
                    bean.onClickListener.invoke()
                    return@setOnMenuItemClickListener true
                }
                menuItem.setIcon(bean.icon)
                menuItem.setShowAsAction(bean.showAsAction)
            }
        }
    }

    fun initToolbar(){

    }
}