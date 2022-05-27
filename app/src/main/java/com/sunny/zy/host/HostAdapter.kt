package com.sunny.zy.host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import kotlinx.android.synthetic.main.dialog_host_item.view.*

/**
 * Desc 域名配置适配器
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/2/20 18:58
 */
class HostAdapter(list: ArrayList<String>) : BaseRecycleAdapter<String>(list) {

    var onItemDeleteListener: ((Int) -> Unit)? = null

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.dialog_host_item, parent, false)
    }

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        holder.itemView.tv_name.text = getData(position).toString()

        holder.itemView.iv_delete.setOnClickListener {
            onItemDeleteListener?.invoke(position)
        }

    }
}