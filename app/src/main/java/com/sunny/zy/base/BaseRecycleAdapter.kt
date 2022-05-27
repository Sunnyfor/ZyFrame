package com.sunny.zy.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter基类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/1/5.
 */
abstract class BaseRecycleAdapter<T>(private var list: ArrayList<T>) :
    RecyclerView.Adapter<BaseRecycleViewHolder>() {
    private var isDouble = false
    private var onItemClickListener: ((view: View, position: Int) -> Unit)? = null
    lateinit var context: Context

    /*
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecycleViewHolder {
        context = parent.context
        return BaseRecycleViewHolder(initLayout(parent, viewType), onItemClickListener)
    }

    /*
     * 绑定数据
     */
    abstract override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int)


    override fun getItemCount(): Int = list.size

    abstract fun initLayout(parent: ViewGroup, viewType: Int): View

    fun getData() = list

    fun getData(position: Int): T = list[position]

    /*
     * 子条目点击事件
     */
    fun setOnItemClickListener(onItemClickListener: ((view: View, position: Int) -> Unit)) {
        this.onItemClickListener = onItemClickListener
    }

}

