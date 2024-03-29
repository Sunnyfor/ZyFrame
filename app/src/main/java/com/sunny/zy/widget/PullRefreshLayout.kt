package com.sunny.zy.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.bean.PlaceholderBean
import com.sunny.zy.config.ZyBaseConfig


/**
 * Desc 下拉刷新自定义View
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2019/11/7 23:24
 */
@Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST")
class PullRefreshLayout : SmartRefreshLayout {

    val rootView: FrameLayout by lazy {
        FrameLayout(context)
    }

    private var contentView: View? = null


    var enableRefresh: Boolean = true
        set(value) {
            field = value
            setEnableRefresh(field)
        }

    var enableLoadMore: Boolean = true
        set(value) {
            field = value
            setEnableLoadMore(field)
        }

    /**
     * 不赋值的情况下不显示占位图
     */
    private val defaultLayoutParams =
        FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    var isReverse = false //如果为true 就标识下拉加载  上拉刷新

    var default = 1 //默认首页

    var page = default

    var loadData: (() -> Unit)? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    val headerView by lazy {
        ClassicsHeader(context)
    }

    val footerView by lazy {
        ClassicsFooter(context)
    }

    val defaultStateView by lazy {
        object : DefaultStateView(ZyBaseConfig.createStateView) {
            override fun getStateViewParent(): ViewGroup {
                return rootView
            }
        }
    }

    private fun init() {
        setRefreshHeader(headerView)
        setRefreshFooter(footerView)

        setEnableAutoLoadMore(true)//开启自动加载功能

        setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                if (isReverse) {
                    page = default
                } else {
                    page++
                }
                loadData?.invoke()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                if (isReverse) {
                    page++
                } else {
                    page = default
                }
                loadData?.invoke()
            }
        })
        addView(rootView, defaultLayoutParams)
    }

    fun setContentView(contentView: View) {
        setContentView(contentView, defaultLayoutParams)
    }


    /**
     * 设置刷新内容
     */
    fun setContentView(contentView: View, layoutParams: ViewGroup.LayoutParams) {
        if (contentView.parent != null) {
            (contentView.parent as ViewGroup).removeView(contentView)
        }
        rootView.removeView(this.contentView)
        this.contentView = contentView
        rootView.addView(contentView, layoutParams)
    }

    /**
     *  通过反射创建View
     */
    fun <T : View> setContentView(contentView: Class<T>) {
        setContentView(contentView, defaultLayoutParams)
    }


    fun <T : View> setContentView(contentView: Class<T>, layoutParams: ViewGroup.LayoutParams) {
        rootView.removeView(this.contentView)
        this.contentView = contentView.getConstructor(Context::class.java).newInstance(context)
        rootView.addView(this.contentView, layoutParams)
    }


    fun <T> addData(adapter: BaseRecycleAdapter<T>, data: ArrayList<T>) {
        addData(adapter, -1, data)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun <T> addData(adapter: BaseRecycleAdapter<T>, index: Int = -1, data: ArrayList<T>) {
        if (page == 1) {
            adapter.getData().clear()
            finishRefreshData()
        } else {
            if (data.isEmpty()) {
                page--
                finishLoadMoreNoMoreData()
            } else {
                finishLoadMoreData()
            }
        }

        if (index < 0) {
            if (isReverse) {
                adapter.getData().addAll(0, data)
                if (data.size == adapter.getData().size) {
                    adapter.notifyDataSetChanged()
                    getRecyclerView()?.scrollToPosition(adapter.getData().size - 1)
                } else {
                    adapter.notifyDataSetChanged()
                }
            } else {
                adapter.getData().addAll(data)
                adapter.notifyDataSetChanged()
            }
        } else {
            if (isReverse) {
                adapter.getData().addAll(data)
            } else {
                adapter.getData().addAll(index, data)
            }
            adapter.notifyDataSetChanged()
        }

        updateEmptyView(adapter.getData())
    }


    fun finishRefreshData(): RefreshLayout {
        if (isReverse) {
            return super.finishLoadMore()
        }
        return super.finishRefresh()

    }

    fun finishLoadMoreData(): RefreshLayout {
        if (isReverse) {
            return super.finishRefresh()
        }
        return super.finishLoadMore()
    }


    fun finishLoadMoreNoMoreData(): RefreshLayout {
        if (isReverse) {
            return finishRefreshWithNoMoreData()
        }
        return finishLoadMoreWithNoMoreData()
    }

    fun <T> deleteData(adapter: BaseRecycleAdapter<T>, index: Int) {
        adapter.getData().removeAt(index)
        adapter.notifyItemRemoved(index)
        adapter.notifyItemRangeChanged(index, adapter.itemCount)
        updateEmptyView(adapter.getData())
    }

    fun <T> deleteData(adapter: BaseRecycleAdapter<T>, data: T) {
        val index = adapter.getData().indexOf(data)
        deleteData(adapter, index)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun <T> deleteData(adapter: BaseRecycleAdapter<T>, data: ArrayList<T>) {
        adapter.getData().removeAll(data.toSet())
        adapter.notifyDataSetChanged()
        updateEmptyView(adapter.getData())
    }

    fun <T> updateEmptyView(data: ArrayList<T>) {
        if (data.isEmpty()) {
            val errorViewBean = PlaceholderBean()
            defaultStateView.showError(errorViewBean)

        } else {
            defaultStateView.hideError()
        }
    }

    fun getRecyclerView(): RecyclerView {
        if (contentView !is RecyclerView) {
            setRecyclerView()
        }
        return contentView as RecyclerView
    }

    override fun onFinishInflate() {
        if (childCount < 4) {
            setRecyclerView()
        } else {
            contentView = getChildAt(childCount - 1)
            contentView?.let {
                val layoutParams = it.layoutParams
                setContentView(it, layoutParams)
                removeView(it)
            }
        }
        super.onFinishInflate()
    }

    private fun setRecyclerView() {
        contentView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
        }
        setContentView(contentView ?: return)
    }
}