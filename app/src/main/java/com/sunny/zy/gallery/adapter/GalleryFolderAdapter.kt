package com.sunny.zy.gallery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.bean.GalleryFolderBean
import kotlinx.android.synthetic.main.zy_item_gallery_folder.view.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22
 */
class GalleryFolderAdapter : BaseRecycleAdapter<GalleryFolderBean>(arrayListOf()) {

    var selectIndex = 0

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        val data = getData(position)
        Glide.with(context)
            .load(data.cover?.uri ?: "")
            .into(holder.itemView.iv_gallery_photo)

        holder.itemView.tv_gallery_name.text = data.name ?: "未命名"
        holder.itemView.tv_gallery_count.text = ("(${data.list.size})")

        if (selectIndex == position) {
            holder.itemView.v_select.visibility = View.VISIBLE
        } else {
            holder.itemView.v_select.visibility = View.GONE
        }
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_folder, parent, false)
    }
}