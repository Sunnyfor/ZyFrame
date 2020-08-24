package com.sunny.zy.base

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * Created by zhangye on 2018/1/5.
 */
@Suppress("UNCHECKED_CAST")
class BaseRecycleViewHolder(
    itemView: View,
    onItemClickListener: ((view: View, position: Int) -> Unit)?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val viewMap = SparseArray<View>()
    private var onItemClickListener: ((view: View, position: Int) -> Unit)? = null

    init {
        if (onItemClickListener != null) {
            this.onItemClickListener = onItemClickListener
            itemView.setOnClickListener(this)
        }
    }

    fun <T : View> getView(id: Int): T {
        if (viewMap.get(id) != null) {
            return viewMap.get(id) as T
        }
        val view = itemView.findViewById(id) as T
        viewMap.put(id, view)
        return view
    }

    override fun onClick(v: View) {
        onItemClickListener?.invoke(v, adapterPosition)
    }
}