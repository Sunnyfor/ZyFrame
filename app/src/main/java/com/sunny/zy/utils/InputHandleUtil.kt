package com.sunny.zy.utils

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.sunny.zy.base.BaseActivity


/**
 * Desc 用于处理全屏模式下输入框被遮挡问题
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/3/17 14:49
 */
class InputHandleUtil(var activity: BaseActivity) : PopupWindow(activity) {
    /**
     * 布局高度发生改变的监听回调
     */
    var onLayoutSizeChangeListener: OnLayoutSizeChangeListener? = null

    private val parentView by lazy {
        LinearLayout(activity)
    }


    init {

        parentView.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        parentView.setBackgroundResource(com.sunny.zy.R.color.color_black)
        parentView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = getParentViewRect()
            var keyboardHeight: Int = DensityUtil.screenHeight() - (rect.bottom - rect.top) - DensityUtil.getStatusBarHeight()

            if (DensityUtil.hasNavBar()) {
                keyboardHeight -= DensityUtil.getNavBarHeight()
            }
            if (keyboardHeight < 100) {
                keyboardHeight = 0
            }
            val keyboardOpen = keyboardHeight > 0
            onLayoutSizeChangeListener?.onChange(keyboardHeight, keyboardOpen)

        }

        contentView = parentView
        softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(0))

        activity.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (!isShowing) {
                    activity.getFitWindowsLinearLayout().post {
                        showAtLocation(activity.getFitWindowsLinearLayout(), Gravity.NO_GRAVITY, 0, 0)
                    }
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                dismiss()
            }
        })
    }


    private fun getParentViewRect(): Rect {
        val rect = Rect()
        parentView.getWindowVisibleDisplayFrame(rect)
        return rect
    }


    interface OnLayoutSizeChangeListener {
        fun onChange(value: Int, keyboardOpen: Boolean)
    }
}