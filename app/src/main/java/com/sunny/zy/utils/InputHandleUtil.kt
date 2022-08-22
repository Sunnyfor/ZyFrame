package com.sunny.zy.utils

import android.graphics.Rect
import android.util.DisplayMetrics
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

    var keyboardOpen = false

    private val parentView by lazy {
        LinearLayout(activity)
    }

    private var screenHeight = 0

    init {

        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getRealMetrics(dm)
        screenHeight = dm.heightPixels

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
                    activity?.getStateViewParent()?.post {
                        showAtLocation(activity?.getStateViewParent(), Gravity.NO_GRAVITY, 0, 0)
                    }
                    parentView.viewTreeObserver.addOnGlobalLayoutListener(this@InputHandleUtil)
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                parentView.viewTreeObserver.removeOnGlobalLayoutListener(this@InputHandleUtil)
                dismiss()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                activity = null
                onLayoutSizeChangeListener = null
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
        var keyboardHeight: Int = screenHeight - (rect.bottom - rect.top)
        if (keyboardHeight < 100) {
            keyboardHeight = 0
        } else {
            if (!keyboardOpen && keyboardHeight > screenHeight * 0.75) {
                //拦截不正常高度
                return
            }
            keyboardHeight -= DensityUtil.getStatusBarHeight()
        }
        keyboardOpen = keyboardHeight > 0
        onLayoutSizeChangeListener?.onChange(keyboardHeight, keyboardOpen)
    }
}