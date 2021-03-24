package com.sunny.zy.widget

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
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.utils.PlaceholderViewUtil


/**
 * Desc 下拉刷新自定义View
 * Author Zy
 * Date 2019/11/7 23:24
 */
@Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST")
class PullRefreshLayout : SmartRefreshLayout {

    private val rootView: FrameLayout by lazy {
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
    var placeholderViewUtil: PlaceholderViewUtil? = null

    private val layoutParams =
        FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    var isReverse = false //如果为true 就标识下拉加载  上拉刷新

    var page = 1

    var loadData: (() -> Unit)? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    private fun init() {
        setRefreshHeader(ClassicsHeader(context))
        setRefreshFooter(ClassicsFooter(context))
        setEnableAutoLoadMore(true)//开启自动加载功能
        addView(rootView, layoutParams)
        setContentView(RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
        })
        setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
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
                loadData?.invoke()
            }
        })

    }

    /**
     * 设置刷新内容
     */
    fun setContentView(contentView: View) {
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
        rootView.removeView(this.contentView)
        this.contentView = contentView.getConstructor(Context::class.java).newInstance(context)
        rootView.addView(this.contentView, layoutParams)
    }

    fun <T> addData(adapter: BaseRecycleAdapter<T>, data: T) {
        addData(adapter, -1, arrayListOf(data))
    }

    fun <T> addData(adapter: BaseRecycleAdapter<T>, data: ArrayList<T>) {
        addData(adapter, -1, data)
    }

    fun <T> addData(adapter: BaseRecycleAdapter<T>, index: Int = -1, data: ArrayList<T>) {
        if (page == 1) {
            adapter.getData().clear()
            if (isReverse) {
                finishLoadMore()
            } else {
                finishRefresh()
            }
        } else {
            if (data.isEmpty()) {
                page--
                if (isReverse) {
                    finishRefreshWithNoMoreData()
                } else {
                    finishLoadMoreWithNoMoreData()
                }
            } else {
                if (isReverse) {
                    finishRefresh()
                } else {
                    finishLoadMore()
                }
            }
        }
        if (index < 0) {
            adapter.getData().addAll(data)
        } else {
            adapter.getData().addAll(index, data)
        }
        updateEmptyView(adapter.getData())
        adapter.notifyDataSetChanged()

    }


    fun <T> deleteData(adapter: BaseRecycleAdapter<T>, index: Int) {
        adapter.getData().removeAt(index)
        adapter.notifyDataSetChanged()
        updateEmptyView(adapter.getData())
    }

    fun <T> deleteData(adapter: BaseRecycleAdapter<T>, data: T) {
        adapter.getData().remove(data)
        adapter.notifyDataSetChanged()
        updateEmptyView(adapter.getData())

    }

    fun <T> deleteData(adapter: BaseRecycleAdapter<T>, data: ArrayList<T>) {
        adapter.getData().removeAll(data)
        adapter.notifyDataSetChanged()
        updateEmptyView(adapter.getData())
    }


    private fun <T> updateEmptyView(data: ArrayList<T>) {
        if (data.isEmpty()) {
            placeholderViewUtil?.showView(
                rootView,
                ZyConfig.emptyPlaceholderBean
            )
        } else {
            placeholderViewUtil?.hideView(ZyConfig.emptyPlaceholderBean.viewType)
        }
    }


    fun <T> getContentView(): T? {
        try {
            return contentView as T
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        return null
    }

    fun getRecyclerView(): RecyclerView? {
        return getContentView()
    }
}