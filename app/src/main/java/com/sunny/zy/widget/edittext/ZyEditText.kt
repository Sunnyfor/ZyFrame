package com.sunny.zy.widget.edittext

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


/**
 * 设置拦截器
 * Created by zhangye on 2018/2/6.
 */
class ZyEditText : AppCompatEditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {

        val mFilterList = arrayListOf<InputFilter>()

        //保留XML中过滤器
        filters.forEach {
            mFilterList.add(it)
        }

        //空格空串过滤器
        val spaceFilter = InputFilter { source, _, _, _, _, _ ->
            //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
            if (source.isEmpty()) "" else source
        }

        //特殊字符过滤器
        val specialFilter = InputFilter { source, _, _, _, _, _ ->
            val regex = Regex("[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“。，、？]")
            if (regex.find(source) != null) "" else source
        }

        mFilterList.add(spaceFilter)
        mFilterList.add(specialFilter)

        filters = mFilterList.toTypedArray()
    }
}
