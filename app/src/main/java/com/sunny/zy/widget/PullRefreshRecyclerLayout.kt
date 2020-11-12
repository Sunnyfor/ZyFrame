package com.sunny.zy.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


/**
 * Desc 下拉刷新自定义View
 * Author Zy
 * Date 2019/11/7 23:24
 */
class PullRefreshRecyclerLayout : SmartRefreshLayout {

    val rootView: FrameLayout by lazy {
        FrameLayout(context)
    }

    val recyclerView: RecyclerView by lazy {
        RecyclerView(context)
    }

    private val layoutParams =
        FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        setRefreshHeader(ClassicsHeader(context))
        setRefreshFooter(ClassicsFooter(context))
        setEnableAutoLoadMore(true)//开启自动加载功能
        rootView.addView(recyclerView, layoutParams)
        addView(rootView, layoutParams)
    }

    /**
     * 禁止刷新和加载数据
     */
    fun setUnEnableRefreshAndLoad(enableRefresh: Boolean, enableLoadMore: Boolean) {
        setEnableRefresh(enableRefresh)
        setEnableLoadMore(enableLoadMore)
    }
}