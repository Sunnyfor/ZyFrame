package com.sunny.zy.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.sunny.zy.R


/**
 * Desc 下拉刷新
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
 * Date 2019/11/7 23:24
 */
class PullRefreshRecyclerLayout : SmartRefreshLayout {

    private val fmLayout: FrameLayout by lazy {
        FrameLayout(context)
    }

    val recyclerView: RecyclerView by lazy {
        RecyclerView(context)
    }

    private val emptyView: View by lazy {
        View.inflate(context, R.layout.zy_layout_error, null)
    }

    private val layoutParams =
        FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var isShowEmptyView = true

    init {
        setRefreshHeader(ClassicsHeader(context))
        setRefreshFooter(ClassicsFooter(context))
        setEnableAutoLoadMore(true)//开启自动加载功能
        fmLayout.addView(recyclerView, layoutParams)
        addView(fmLayout, layoutParams)
    }

    /**
     * 禁止刷新和加载数据
     */
    fun setUnEnableRefreshAndLoad(enableRefresh: Boolean, enableLoadMore: Boolean) {
        setEnableRefresh(enableRefresh)
        setEnableLoadMore(enableLoadMore)
    }

    fun showEmptyView() {
        if (isShowEmptyView){
            hideEmptyView()
            fmLayout.addView(emptyView, layoutParams)
        }

    }

    fun hideEmptyView() {
        if (isShowEmptyView){
            fmLayout.removeView(emptyView)
        }
    }
}