package com.sunny.zy.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.sunny.zy.BuildConfig
import com.sunny.zy.R


/**
 * Desc 自定义EditText清除按钮和震动效果
 * Author ZY
 * Date 2019/12/20
 */
class ClearEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.editTextStyle) :
    AppCompatEditText(context, attrs, defStyle), OnFocusChangeListener, TextWatcher {

    /**
     * 是否展示删除图标
     */
    var isShowClearBtn: Boolean = true

    /**
     * 删除按钮的引用
     */
    private var mClearDrawable: Drawable? = null

    /**
     * 控件是否有焦点
     */
    private var hasFocus: Boolean = false

    init {
        init()
    }

    private fun init() {
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = compoundDrawables[2]
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(context, R.drawable.sel_edit_clear)
        }

        if (BuildConfig.DEBUG && mClearDrawable == null) {
            error("Assertion failed")
        }
        mClearDrawable?.setBounds(0, 0, mClearDrawable?.intrinsicWidth ?: 0, mClearDrawable?.intrinsicHeight ?: 0)

        // 默认设置隐藏图标
        setClearIconVisible(false)
        // 设置焦点改变的监听
        onFocusChangeListener = this
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this)
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (compoundDrawables[2] != null) {

                val touchable = event.x > width - totalPaddingRight && event.x < width - paddingRight

                if (touchable) {
                    this.setText("")
                }
            }
        }

        return super.onTouchEvent(event)
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        this.hasFocus = hasFocus
        if (hasFocus) {
            setClearIconVisible(text?.isNotEmpty() == true)
        } else {
            setClearIconVisible(false)
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     */
    private fun setClearIconVisible(visible: Boolean) {
        if (!isShowClearBtn) {
            return
        }
        val right = if (visible) mClearDrawable else null
        setCompoundDrawables(
            compoundDrawables[0],
            compoundDrawables[1], right, compoundDrawables[3]
        )
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (hasFocus) {
            setClearIconVisible(s.isNotEmpty())
        }
    }

    override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int,
        after: Int
    ) {

    }

    override fun afterTextChanged(s: Editable) {

    }

    /**
     * 设置晃动动画
     */
    fun setShakeAnimation() {
        this.startAnimation(shakeAnimation())
    }

    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     */
    private fun shakeAnimation(counts: Int? = 5): Animation {
        val translateAnimation = TranslateAnimation(0f, 10f, 0f, 0f)
        translateAnimation.interpolator = CycleInterpolator(counts?.toFloat() ?: 0f)
        translateAnimation.duration = 1000
        // translateAnimation.setFillAfter(true);
        return translateAnimation
    }

}