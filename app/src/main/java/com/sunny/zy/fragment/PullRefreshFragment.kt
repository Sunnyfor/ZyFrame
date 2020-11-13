package com.sunny.zy.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
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
    open var layoutManager: RecyclerView.LayoutManager? = null
    open var page = 1
    open var loadData: (() -> Unit)? = null
    open var enableRefresh: Boolean = true
    open var enableLoadMore: Boolean = true
    open var isShowEmptyView = true //是否显示占位图
    open var isReverse = false //如果为true 就标识下拉加载  上拉刷新

    private var pullRefreshLayout: PullRefreshRecyclerLayout? = null

    /**
     * 此view的操作需要在Fragment加载完成后进行
     */
    open val recyclerView: RecyclerView?
        get() = pullRefreshLayout?.recyclerView


    override fun initLayout(): Any {
        pullRefreshLayout = PullRefreshRecyclerLayout(requireContext())
        return pullRefreshLayout!!
    }

    override fun initView() {
        pullRefreshLayout?.setUnEnableRefreshAndLoad(enableRefresh, enableLoadMore)
        recyclerView?.adapter = adapter
        layoutManager = layoutManager ?: LinearLayoutManager(context)

        pullRefreshLayout?.recyclerView?.layoutManager = layoutManager
        pullRefreshLayout?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                if (isReverse) {
                    page = 1
                } else {
                    page++
                }

                loadData?.invoke()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                if (isReverse) {
                    page++
                } else {
                    page = 1
                }
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
        layoutManager = null
        adapter = null
        pullRefreshLayout?.removeAllViews()
        pullRefreshLayout = null
    }

    open fun addData(data: ArrayList<T>) {
        addData(-1, data)
    }

    open fun addData(index: Int, data: ArrayList<T>) {
        if (page == 1) {
            adapter?.clearData()
            pullRefreshLayout?.finishRefresh()
        } else {
            if (data.isEmpty()) {
                page--
                pullRefreshLayout?.finishLoadMoreWithNoMoreData()
            } else {
                pullRefreshLayout?.finishLoadMore()
            }
        }
        if (index < 0) {
            adapter?.addData(data)
        } else {
            adapter?.addData(index, data)
        }
        updateEmptyView()
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


    private fun updateEmptyView() {
        if (isShowEmptyView) {
            if (getAllData()?.isEmpty() == true) {
                showPlaceholder(
                    pullRefreshLayout?.rootView ?: return,
                    ZyConfig.emptyPlaceholderBean
                )
            } else {
                hidePlaceholder(PlaceholderBean.emptyData)
            }
        }
    }


    open fun getAllData() = adapter?.getData()
}