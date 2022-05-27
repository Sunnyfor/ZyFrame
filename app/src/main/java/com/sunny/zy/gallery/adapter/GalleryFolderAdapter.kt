package com.sunny.zy.gallery.adapter

import android.content.ContentUris
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.bean.GalleryFolderBean
import com.sunny.zy.utils.GlideApp
import com.sunny.zy.utils.LogUtil
import kotlinx.android.synthetic.main.zy_item_gallery_folder.view.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 18:59
 */
class GalleryFolderAdapter : BaseRecycleAdapter<GalleryFolderBean>(arrayListOf()) {

    var selectIndex = 0

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        val data = getData(position)
        GlideApp.with(context)
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