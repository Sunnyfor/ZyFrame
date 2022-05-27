package com.sunny.zy.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout


/**
 * Desc 用于处理全屏模式下输入框被遮挡问题
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/3/17 14:49
 */
class InputHandleUtil {
    private var mRootView: ViewGroup? = null
    private var rootViewParams: ViewGroup.LayoutParams? = null
    private var rootViewMargin = 0
    private var lastRootLayoutHeight = 0
    private var scrollViewParams: ViewGroup.MarginLayoutParams? = null
    private var scrollViewMargin = 0
    private var ignoreViewParams: ViewGroup.MarginLayoutParams? = null

    /**
     * 布局高度发生改变的监听回调
     */
    var onLayoutSizeChangeListener: OnLayoutSizeChangeListener? = null


    /**
     * 处理包含输入框的容器方法
     * @param rootView 基本View
     * @param scrollView 只处理ScrollView内部view
     * @param ignoreView 忽略scrollView外部的View
     */
    fun handleInputView(
        rootView: ViewGroup,
        scrollView: ScrollView? = null,
        ignoreView: View? = null
    ) {
        mRootView = rootView
        rootViewParams = mRootView?.layoutParams
        setRootViewBottomMargin(0, true)

        mRootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(scrollView, ignoreView)
        }
        lastRootLayoutHeight = getRootLayoutHeight()

        scrollView?.let {
            scrollViewParams = it.layoutParams as ViewGroup.MarginLayoutParams
            scrollViewMargin = scrollViewParams?.bottomMargin ?: 0
        }
        ignoreView?.let {
            ignoreViewParams = it.layoutParams as ViewGroup.MarginLayoutParams
        }
    }


    private fun possiblyResizeChildOfContent(scrollView: ScrollView?, ignoreView: View? = null) {
        val currentRootLayoutHeight = getRootLayoutHeight()
        if (currentRootLayoutHeight != lastRootLayoutHeight) {
            val rootLayoutHeight = mRootView?.rootView?.height ?: 0
            val keyBoardHeight = rootLayoutHeight - currentRootLayoutHeight
            if (keyBoardHeight > rootLayoutHeight / 4) {
                if (scrollView != null) {
                    val height = ignoreView?.height ?: 0
                    val topMargin = ignoreViewParams?.topMargin ?: 0
                    val bottomMargin = ignoreViewParams?.bottomMargin ?: 0
                    val value = keyBoardHeight - height - topMargin - bottomMargin
                    scrollViewParams?.bottomMargin = value
                    scrollView.requestLayout()
                    onLayoutSizeChangeListener?.onChange(value)

                } else {
                    val value = rootViewMargin + keyBoardHeight
                    setRootViewBottomMargin(value, false)
                    onLayoutSizeChangeListener?.onChange(value)
                }
            } else {
                if (scrollView != null) {
                    scrollViewParams?.bottomMargin = scrollViewMargin
                    //重绘scrollView布局
                    scrollView.requestLayout()
                    onLayoutSizeChangeListener?.onChange(scrollViewMargin)
                } else {
                    setRootViewBottomMargin(rootViewMargin, false)
                    onLayoutSizeChangeListener?.onChange(rootViewMargin)
                }
            }
            lastRootLayoutHeight = currentRootLayoutHeight
        }
    }

    private fun setRootViewBottomMargin(margin: Int, isInit: Boolean) {
        when (rootViewParams) {
            is LinearLayout.LayoutParams -> {
                if (isInit) {
                    rootViewMargin = (rootViewParams as LinearLayout.LayoutParams).bottomMargin
                } else {
                    (rootViewParams as LinearLayout.LayoutParams).bottomMargin = margin
                }
            }
            is RelativeLayout.LayoutParams -> {
                if (isInit) {
                    rootViewMargin = (rootViewParams as RelativeLayout.LayoutParams).bottomMargin
                } else {
                    (rootViewParams as RelativeLayout.LayoutParams).bottomMargin = margin
                }
            }
            is FrameLayout.LayoutParams -> {
                if (isInit) {
                    rootViewMargin = (rootViewParams as FrameLayout.LayoutParams).bottomMargin
                } else {
                    (rootViewParams as FrameLayout.LayoutParams).bottomMargin = margin
                }
            }
            is ConstraintLayout.LayoutParams -> {
                if (isInit) {
                    rootViewMargin = (rootViewParams as ConstraintLayout.LayoutParams).bottomMargin
                } else {
                    (rootViewParams as ConstraintLayout.LayoutParams).bottomMargin = margin
                }
            }
        }
        mRootView?.requestLayout()
    }

    private fun getRootLayoutHeight(): Int {
        val r = Rect()
        mRootView?.getWindowVisibleDisplayFrame(r)
        return r.bottom
    }


    interface OnLayoutSizeChangeListener {
        fun onChange(value: Int)
    }
}