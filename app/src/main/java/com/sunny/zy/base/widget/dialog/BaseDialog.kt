package com.sunny.zy.base.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.contrarywind.view.WheelView
import com.sunny.zy.R

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2022/3/1 15:07
 */
open class BaseDialog : Dialog {

    var layout: Any

    constructor(context: Context, @LayoutRes layoutRes: Int) : super(context) {
        layout = layoutRes
    }

    constructor(context: Context, view: View) : super(context) {
        layout = view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawableResource(R.color.color_transparent)

        when (layout) {
            is Int -> {
                setContentView(layout as Int)
            }
            is View -> {
                setContentView(layout as View)
            }
        }
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
    }


    fun setWheelViewStyle(wheelView: WheelView) {
        wheelView.setTypeface(Typeface.DEFAULT)
        wheelView.setDividerColor(ContextCompat.getColor(context, R.color.color_transparent))
        wheelView.setItemsVisibleCount(5)
        wheelView.setLineSpacingMultiplier(2f)
    }
}