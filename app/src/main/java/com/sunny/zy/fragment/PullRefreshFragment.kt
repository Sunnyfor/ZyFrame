package com.sunny.zy.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sunny.zy.R
import com.sunny.zy.base.BaseFragment
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.PlaceholderBean
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.widget.PullRefreshRecyclerLayout

/**
 * Desc 下拉刷新，上拉加载
 * Author Zy
 * Date 2020/6/4 16:05
 */
open class PullRefreshFragment<T> : BaseFragment() {
    open var adapter: BaseRecycleAdapter<T>? = null
    open var page = 1
    open var loadData: (() -> Unit)? = null
    open var enableRefresh: Boolean = true
    open var enableLoadMore: Boolean = true
    open var isShowEmptyView = true

    private val pullRefreshLayout: PullRefreshRecyclerLayout by lazy {
        PullRefreshRecyclerLayout(context)
    }

    open val recyclerView: RecyclerView
        get() = pullRefreshLayout.recyclerView

    override fun initLayout() = pullRefreshLayout

    override fun initView() {
        pullRefreshLayout.isShowEmptyView = isShowEmptyView
        pullRefreshLayout.setUnEnableRefreshAndLoad(enableRefresh, enableLoadMore)
        if (adapter != null) {
            recyclerView.adapter = adapter
        }
        if (recyclerView.layoutManager == null) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
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

    override fun onClose() {

    }

    open fun addData(data: ArrayList<T>) {
        addData(-1, data)
    }

    open fun addData(index: Int, data: ArrayList<T>) {
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
        if (index < 0) {
            adapter?.addData(data)
        } else {
            adapter?.addData(index, data)
        }
        adapter?.notifyDataSetChanged()
    }

    open fun deleteData(index: Int) {
        adapter?.deleteData(index)
        adapter?.notifyDataSetChanged()
        updateEmptyView()
    }

    open fun deleteData(data: T) {
        adapter?.deleteData(data)
        adapter?.notifyDataSetChanged()
        updateEmptyView()
    }


    private fun updateEmptyView(data: ArrayList<T>? = null) {
        if ((data ?: getAllData())?.isEmpty() == true) {
            showPlaceholder(pullRefreshLayout.rootView, ZyConfig.emptyPlaceholderBean)
        } else {
            hidePlaceholder(PlaceholderBean.emptyData)
        }
    }


    open fun getAllData() = adapter?.getData()
}