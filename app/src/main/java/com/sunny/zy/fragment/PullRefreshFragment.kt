package com.sunny.zy.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunny.zy.base.BaseFragment
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.widget.PullRefreshLayout

/**
 * Desc 下拉刷新，上拉加载
 * Author Zy
 * Date 2020/6/4 16:05
 */
@Deprecated("后续版本删除这个狗篮子")
open
class PullRefreshFragment<T> : BaseFragment() {
    open var adapter: BaseRecycleAdapter<T>? = null
    open var layoutManager: RecyclerView.LayoutManager? = null

    open var page = 1
        set(value) {
            field = value
            pullRefreshLayout?.page = value
        }

    open var loadData: (() -> Unit)? = null
    open var enableRefresh: Boolean = true
    open var enableLoadMore: Boolean = true
    open var isShowEmptyView = true //是否显示占位图
    open var isReverse = false //如果为true 就标识下拉加载  上拉刷新

    private var pullRefreshLayout: PullRefreshLayout? = null


    override fun initLayout(): Any? {
        if(pullRefreshLayout == null){
            pullRefreshLayout = PullRefreshLayout(requireContext())
        }
        return pullRefreshLayout
    }

    override fun initView() {

        pullRefreshLayout?.enableRefresh = enableRefresh
        pullRefreshLayout?.enableLoadMore = enableLoadMore

        getRecyclerView()?.adapter = adapter
        layoutManager = layoutManager ?: LinearLayoutManager(context)
        getRecyclerView()?.layoutManager = layoutManager

        pullRefreshLayout?.loadData = loadData

    }

    override fun onClickEvent(view: View) {

    }

    override fun loadData() {

    }

    override fun onClose() {
        layoutManager = null
        getRecyclerView()?.adapter = null
    }

    open fun addData(data: T) {
        addData(-1, arrayListOf(data))
    }

    open fun addData(data: ArrayList<T>) {
        addData(-1, data)
    }

    open fun addData(index: Int, data: ArrayList<T>) {
        pullRefreshLayout?.addData( index, data)
    }

    open fun deleteData(index: Int) {
        pullRefreshLayout?.deleteData(index)
    }

    open fun deleteData(data: T) {
        pullRefreshLayout?.deleteData(data)
    }


    open fun getAllData() = adapter?.getData()


    fun getRecyclerView() = pullRefreshLayout?.getContentView<RecyclerView>()
}