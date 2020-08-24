package com.sunny.zy.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sunny.zy.base.BaseFragment
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.widget.PullRefreshRecyclerLayout

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/6/4 16:05
 */
open class PullRefreshFragment<T> : BaseFragment() {
    var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
    var adapter: BaseRecycleAdapter<T>? = null
    var page = 1
    var loadData: (() -> Unit)? = null
    var enableRefresh: Boolean = true
    var enableLoadMore: Boolean = true
    var isShowEmptyView = true

    private val pullRefreshLayout: PullRefreshRecyclerLayout by lazy {
        PullRefreshRecyclerLayout(context)
    }

    override fun setLayout(): Int = 0

    override fun initView() {
        setLayoutView(pullRefreshLayout)
        pullRefreshLayout.isShowEmptyView = isShowEmptyView
        pullRefreshLayout.setUnEnableRefreshAndLoad(enableRefresh, enableLoadMore)
        pullRefreshLayout.recyclerView.adapter = adapter
        pullRefreshLayout.recyclerView.layoutManager = layoutManager

        pullRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                page++
                loadData?.invoke()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                page = 1
                loadData?.invoke()
            }
        })
    }

    override fun onClickEvent(view: View) {

    }

    override fun loadData() {

    }

    override fun close() {

    }


    fun addData(data: ArrayList<T>) {
        if (page == 1) {
            adapter?.clearData()
            pullRefreshLayout.finishRefresh()
            updateEmptyView(data)
        } else {
            if (data.isEmpty()) {
                page--
                pullRefreshLayout.finishLoadMoreWithNoMoreData()
            } else {
                pullRefreshLayout.finishLoadMore()
            }
        }
        adapter?.addData(data)
        adapter?.notifyDataSetChanged()
    }

    fun deleteData(index: Int) {
        adapter?.deleteData(index)
        adapter?.notifyDataSetChanged()
        updateEmptyView()
    }

    fun deleteData(data: T) {
        adapter?.deleteData(data)
        adapter?.notifyDataSetChanged()
        updateEmptyView()
    }


    private fun updateEmptyView(data: ArrayList<T>? = null) {
        if ((data ?: getAllData())?.isEmpty() == true) {
            pullRefreshLayout.showEmptyView()
        } else {
            pullRefreshLayout.hideEmptyView()
        }
    }


    fun getAllData() = adapter?.getData()
}