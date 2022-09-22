package com.sunny.zy.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sunny.zy.R

/**
 * Desc 自定义子条目组件
 * 样式参考：https://dwz.cn/GQG6d3gP
 * Author ZY
 * Date 2020/2/4 16:07
 */
class ItemLayout : RelativeLayout {

    val startImageView: ImageView by lazy {
        ImageView(context).apply {
            id = R.id.iv_start
        }
    }

    val startTextView: TextView by lazy {
        TextView(context).apply {
            id = R.id.tv_start
        }
    }

    val startAfterTextView: TextView by lazy {
        TextView(context)
    }


    var endImage = 0
    val endImageView: ImageView by lazy {
        ImageView(context).apply {
            id = R.id.iv_end
        }
    }

    val endTextView: TextView by lazy {
        TextView(context).apply {
            id = R.id.tv_end
        }
    }

    val endBeforeTextView: TextView by lazy {
        TextView(context)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val defaultMargin = resources.getDimension(R.dimen.dp_10)
        val defaultTextSize = resources.getDimension(R.dimen.sp_14)
        val defaultTextColor = ContextCompat.getColor(context, R.color.font_black)
        val defaultLineColor = ContextCompat.getColor(context, R.color.color_line)
        val defaultLineHeight = resources.getDimension(R.dimen.dp_1)


        val typeArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ItemLayout, defStyleAttr, 0)


        /**
         * 最外层View
         */

        val leftMargin = typeArray.getDimension(R.styleable.ItemLayout_itemLeftMargin, defaultMargin)
        val rightMargin = typeArray.getDimension(R.styleable.ItemLayout_itemRightMargin, defaultMargin)

        val parentLayout = RelativeLayout(context)
        val layout = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layout.leftMargin = leftMargin.toInt()
        layout.rightMargin = rightMargin.toInt()
        addView(parentLayout, layout)


        /**
         * 最左侧图标
         */
        val startImage = typeArray.getResourceId(R.styleable.ItemLayout_startIcon, 0)
        val startImageSize = typeArray.getResourceId(R.styleable.ItemLayout_startIconSize, 0)
        val startImageMargin = typeArray.getDimension(R.styleable.ItemLayout_startIconRightMargin, defaultMargin)

        if (startImage != 0) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.width = context.resources.getDimension(startImageSize).toInt()
            lp.height = context.resources.getDimension(startImageSize).toInt()
            lp.rightMargin = startImageMargin.toInt()
            lp.addRule(CENTER_VERTICAL)
            startImageView.setImageResource(startImage)
            parentLayout.addView(startImageView, lp)
        }


        /**
         * 最左侧文字
         */
        val startText = typeArray.getString(R.styleable.ItemLayout_startText) ?: ""
        val startTextSize = typeArray.getDimension(R.styleable.ItemLayout_startTextSize, defaultTextSize)
        val startTextColor = typeArray.getColor(R.styleable.ItemLayout_startTextColor, defaultTextColor)
        val startTextMargin = typeArray.getDimension(R.styleable.ItemLayout_startTextRightMargin, defaultMargin)

        if (startText.isNotEmpty()) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            lp.rightMargin = startTextMargin.toInt()
            startTextView.text = startText
            startTextView.setTextColor(startTextColor)
            startTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, startTextSize)
            startTextView.gravity = Gravity.CENTER_VERTICAL
            parentLayout.addView(startTextView, lp)
        }

        /**
         * 最左侧文字/图标 —— 文字
         */
        val startAfterText = typeArray.getString(R.styleable.ItemLayout_startAfterText) ?: ""
        val startAfterTextSize = typeArray.getDimension(R.styleable.ItemLayout_startAfterTextSize, defaultTextSize)
        val startAfterTextColor = typeArray.getColor(R.styleable.ItemLayout_startAfterTextColor, defaultTextColor)

        if (startAfterText.isNotEmpty()) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            startAfterTextView.text = startAfterText
            startAfterTextView.setTextColor(startAfterTextColor)
            startAfterTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, startAfterTextSize)
            startAfterTextView.gravity = Gravity.CENTER_VERTICAL

            val mLeft = when {
                startImage != 0 -> startImageView.id
                startText.isNotEmpty() -> startTextView.id
                else -> 0
            }
            if (mLeft != 0) {
                lp.addRule(RIGHT_OF, mLeft)
            }
            parentLayout.addView(startAfterTextView, lp)
        }

        /**
         * 最右侧图标
         */
        endImage = typeArray.getResourceId(R.styleable.ItemLayout_endIcon, 0)
        val endImageSize = typeArray.getResourceId(R.styleable.ItemLayout_endIconSize, 0)
        val endImageMargin = typeArray.getDimension(R.styleable.ItemLayout_endIconLeftMargin, defaultMargin)

        if (endImage != 0) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            lp.width = context.resources.getDimension(endImageSize).toInt()
            lp.height = context.resources.getDimension(endImageSize).toInt()
            lp.leftMargin = endImageMargin.toInt()
            lp.addRule(ALIGN_PARENT_RIGHT)
            lp.addRule(CENTER_VERTICAL)
            endImageView.setImageResource(endImage)
            parentLayout.addView(endImageView, lp)
        }

        /**
         * 最右侧文字
         */
        val endText = typeArray.getString(R.styleable.ItemLayout_endText) ?: ""
        val endTextSize = typeArray.getDimension(R.styleable.ItemLayout_endTextSize, defaultTextSize)
        val endTextColor = typeArray.getColor(R.styleable.ItemLayout_endTextColor, defaultTextColor)
        val endTextMargin = typeArray.getDimension(R.styleable.ItemLayout_endTextLeftMargin, defaultMargin)

        if (endText.isNotEmpty()) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            lp.leftMargin = endTextMargin.toInt()
            if (endImage != 0) {
                lp.addRule(LEFT_OF, endImageView.id)
            } else {
                lp.addRule(ALIGN_PARENT_RIGHT)
            }

            endTextView.text = endText
            endTextView.setTextColor(endTextColor)
            endTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, endTextSize)
            endTextView.gravity = Gravity.CENTER_VERTICAL
            parentLayout.addView(endTextView, lp)
        }


        /**
         * 文字 —— 最右侧文字/图标
         */
        val endBeforeText = typeArray.getString(R.styleable.ItemLayout_endBeforeText) ?: ""
        val endBeforeTextSize = typeArray.getDimension(R.styleable.ItemLayout_endBeforeTextSize, defaultTextSize)
        val endBeforeTextColor = typeArray.getColor(R.styleable.ItemLayout_endBeforeTextColor, defaultTextColor)

        if (endBeforeText.isNotEmpty()) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            endBeforeTextView.text = endBeforeText
            endBeforeTextView.setTextColor(endBeforeTextColor)
            endBeforeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, endBeforeTextSize)
            endBeforeTextView.gravity = Gravity.CENTER_VERTICAL

            val mRight = if (endText.isNotEmpty()) {
                endTextView.id
            } else {
                endImageView.id
            }

            if (mRight != 0) {
                lp.addRule(LEFT_OF, mRight)
            } else {
                lp.addRule(ALIGN_PARENT_RIGHT)
            }

            parentLayout.addView(endBeforeTextView, lp)
        }


        /**
         * 分割线
         */
        val lineColor = typeArray.getColor(R.styleable.ItemLayout_lineColor, defaultLineColor)
        val lineHeight = typeArray.getDimension(R.styleable.ItemLayout_lineHeight, defaultLineHeight)
        val lineVisible = typeArray.getBoolean(R.styleable.ItemLayout_lineVisible, false)

        if (lineVisible) {
            val view = View(context)
            view.setBackgroundColor(lineColor)
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, lineHeight.toInt())
            lp.addRule(ALIGN_PARENT_BOTTOM)
            addView(view, lp)
        }

        typeArray.recycle()
    }

}