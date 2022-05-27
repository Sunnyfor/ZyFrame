package com.sunny.zy.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunny.zy.base.BaseFragment
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.ZyFrameConfig
import com.sunny.zy.utils.PlaceholderViewUtil
import com.sunny.zy.widget.PullRefreshLayout

/**
 * Desc 下拉刷新，上拉加载
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/6/4 16:05
 */
open class PullRefreshFragment<T> : BaseFragment() {
    open var adapter: BaseRecycleAdapter<T>? = null
    open var layoutManager: RecyclerView.LayoutManager? = null

    var defaultPage = 1

    var page: Int = defaultPage
        set(value) {
            field = value
            pullRefreshLayout?.page = value
        }
        get() {
            return pullRefreshLayout?.page ?: defaultPage
        }

    //如果为true 就标识下拉加载  上拉刷新

    var isReverse: Boolean = false

    open var loadData: (() -> Unit)? = null
    open var enableRefresh: Boolean = true
    open var enableLoadMore: Boolean = true
    open var isShowEmptyView = true //是否显示占位图

    private var pullRefreshLayout: PullRefreshLayout? = null


    override fun initLayout(): Any? {
        if (pullRefreshLayout == null) {
            pullRefreshLayout = PullRefreshLayout(requireContext())
        }
        return pullRefreshLayout
    }

    override fun initView() {

        pullRefreshLayout?.enableRefresh = enableRefresh
        pullRefreshLayout?.enableLoadMore = enableLoadMore
        pullRefreshLayout?.isReverse = isReverse

        if (isShowEmptyView) {
            pullRefreshLayout?.placeholderViewUtil = PlaceholderViewUtil()
        }
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
        pullRefreshLayout?.addData(adapter ?: return, index, data)
    }

    open fun deleteData(index: Int) {
        pullRefreshLayout?.deleteData(adapter ?: return, index)
    }

    open fun deleteData(data: T) {
        pullRefreshLayout?.deleteData(adapter ?: return, data)
    }


    open fun getAllData() = adapter?.getData()


    open fun getRecyclerView() = pullRefreshLayout?.getContentView<RecyclerView>()


    override fun showLoading() {
        pullRefreshLayout?.rootView?.let {
            showPlaceholder(it, ZyFrameConfig.loadingPlaceholderBean)
        }
    }
}