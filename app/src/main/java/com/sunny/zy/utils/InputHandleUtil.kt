package com.sunny.zy.utils

import android.graphics.Rect
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
class InputHandleUtil(var activity: BaseActivity?) : PopupWindow(activity), ViewTreeObserver.OnGlobalLayoutListener {
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

        contentView = parentView
        softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        activity?.lifecycle?.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (!isShowing) {
                    LogUtil.i("显示popupWindow")
                    parentView.viewTreeObserver.addOnGlobalLayoutListener(this@InputHandleUtil)
                    activity?.getFitWindowsLinearLayout()?.post {
                        showAtLocation(activity?.getFitWindowsLinearLayout(), Gravity.NO_GRAVITY, 0, 0)
                    }
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                LogUtil.i("关闭popupWindow")
                parentView.viewTreeObserver.removeOnGlobalLayoutListener(this@InputHandleUtil)
                dismiss()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                LogUtil.i("销毁popupWindow")
                activity = null
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

    override fun onGlobalLayout() {
        val rect = getParentViewRect()
        var keyboardHeight: Int = DensityUtil.screenHeight() - (rect.bottom - rect.top)
        if (keyboardHeight < 100) {
            keyboardHeight = 0
        }else{
            if (activity?.getFitWindowsLinearLayout()?.height == DensityUtil.screenHeight()) {
                keyboardHeight -= DensityUtil.getStatusBarHeight()
            }
        }
        val keyboardOpen = keyboardHeight > 0
        onLayoutSizeChangeListener?.onChange(keyboardHeight, keyboardOpen)
    }
}