package com.sunny.zy.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.bean.GalleryFolderBean

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22
 */
class GalleryFolderAdapter : BaseRecycleAdapter<GalleryFolderBean>(arrayListOf()) {

    var selectIndex = 0

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        val data = getData(position)
        Glide.with(context)
            .load(data.cover?.uri ?: "")
            .into(holder.getView(R.id.ivGalleryPhoto))

        holder.getView<TextView>(R.id.tvGalleryName).text = data.name ?: "未命名"
        val countStr = "(${data.list.size})"
        holder.getView<TextView>(R.id.tvGalleryCount).text = countStr

        holder.getView<View>(R.id.vSelect).visibility = if (selectIndex == position) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_folder, parent, false)
    }
}