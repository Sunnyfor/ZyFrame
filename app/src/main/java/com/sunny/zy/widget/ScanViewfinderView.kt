package com.sunny.zy.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sunny.zy.R

/**
 * Desc 二维码边框
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/1/5 16:42
 */
class ScanViewfinderView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var mWidth = 0
    private var mHeight = 0
    private var lineWidth = context.resources.getDimension(R.dimen.dp_5)
    private var lineHeight = context.resources.getDimension(R.dimen.dp_20)

    private var slideSize = lineWidth

    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.color_theme)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredHeight
        mHeight = measuredWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0F, 0F, lineHeight, lineWidth, paint)
        canvas?.drawRect(0F, lineWidth, lineWidth, lineHeight, paint)

        canvas?.drawRect(mWidth - lineHeight, 0F, mWidth.toFloat(), lineWidth, paint)
        canvas?.drawRect(mWidth - lineWidth, lineWidth, mWidth.toFloat(), lineHeight, paint)

        canvas?.drawRect(0F, mHeight - lineWidth, lineHeight, mHeight.toFloat(), paint)
        canvas?.drawRect(0F, mHeight - lineHeight, lineWidth, mHeight.toFloat(), paint)

        canvas?.drawRect(mWidth - lineHeight, mHeight - lineWidth, mWidth.toFloat(), mHeight.toFloat(), paint)
        canvas?.drawRect(mWidth - lineWidth, mHeight - lineHeight, mWidth.toFloat(), mHeight.toFloat(), paint)

        canvas?.drawRect(lineHeight,slideSize+lineWidth,mWidth - lineHeight,slideSize+ lineWidth*2,paint)
        slideSize += lineWidth
        slideSize =(slideSize% (mHeight - lineWidth *3))
        postInvalidateDelayed(15)
    }
}