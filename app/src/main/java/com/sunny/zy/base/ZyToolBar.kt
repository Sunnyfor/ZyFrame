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

    private var layoutRes = 0

    private lateinit var titleView: View

    constructor(context: Context, layoutRes: Int) : super(context) {
        this.layoutRes = layoutRes

        if (layoutRes != 0) {
            setContentInsetsAbsolute(0, 0)
            setContentInsetsRelative(0, 0)
            titleView = LayoutInflater.from(context).inflate(layoutRes, this, false)
            addView(titleView)
        }
        setBackgroundResource(R.color.colorPrimary)
        setTitleTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
        setSubtitleTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setTitle(resId: Int) {
        if (layoutRes == 0) {
            super.setTitle(resId)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<TextView>(R.id.zy_tv_title).text = resources.getString(resId)
            return
        }
    }

    override fun setTitle(title: CharSequence?) {
        if (layoutRes == 0) {
            super.setTitle(title)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<TextView>(R.id.zy_tv_title).text = title
            return
        }
    }

    override fun setTitleTextColor(color: ColorStateList) {
        if (layoutRes == 0) {
            super.setTitleTextColor(color)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<TextView>(R.id.zy_tv_title).setTextColor(color)
            return
        }
    }

    override fun setTitleTextColor(color: Int) {
        if (layoutRes == 0) {
            super.setTitleTextColor(color)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<TextView>(R.id.zy_tv_title).setTextColor(color)
            return
        }
    }

    override fun setNavigationIcon(resId: Int) {
        if (layoutRes == 0) {
            super.setNavigationIcon(resId)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<AppCompatImageButton>(R.id.zy_ib_back).let {
                if (resId != 0) {
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
                it.setImageResource(resId)
            }
            return
        }
    }

    override fun setNavigationIcon(icon: Drawable?) {
        if (layoutRes == 0) {
            super.setNavigationIcon(icon)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<AppCompatImageButton>(R.id.zy_ib_back).let {
                if (icon != null) {
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
                it.setImageDrawable(icon)
            }
            return
        }
    }

    override fun setNavigationOnClickListener(listener: OnClickListener?) {

        if (layoutRes == 0) {
            super.setNavigationOnClickListener(listener)
            return
        }
        if (layoutRes == R.layout.zy_default_title) {
            getView<AppCompatImageButton>(R.id.zy_ib_back).setOnClickListener(listener)
            return
        }
    }

    override fun inflateMenu(resId: Int) {
        if (layoutRes == 0 || layoutRes == R.layout.zy_default_title) {
            super.inflateMenu(resId)
            return
        }
    }

    override fun getMenu(): Menu? {
        if (layoutRes == 0) {
            return super.getMenu()
        }

        if (layoutRes == R.layout.zy_default_title) {
            return getView<ActionMenuView>(R.id.zy_menu_view).menu
        }

        return null
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


    private fun <T : View> getView(id: Int): T {
        return titleView.findViewById(id)
    }
}