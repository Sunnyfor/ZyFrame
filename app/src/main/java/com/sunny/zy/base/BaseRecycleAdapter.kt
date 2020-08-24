package com.sunny.zy.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * baseAdapter
 * Created by zhangye on 2018/1/5.
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
        return BaseRecycleViewHolder(setLayout(parent, viewType), onItemClickListener)
    }

    /*
     * 绑定数据
     */
    abstract override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int)


    override fun getItemCount(): Int = list.size

    abstract fun setLayout(parent: ViewGroup, viewType: Int): View

    fun getData() = list

    fun getData(position: Int): T = list[position]

    fun addData(arrayList: ArrayList<T>) {
        list.addAll(arrayList)
    }

    fun deleteData(position: Int) {
        list.removeAt(position)
    }

    fun deleteData(data: T) {
        list.remove(data)
    }

    fun clearData() {
        list.clear()
    }

    /*
     * 子条目点击事件
     */
    fun setOnItemClickListener(onItemClickListener: ((view: View, position: Int) -> Unit)) {
        this.onItemClickListener = onItemClickListener
    }

}

