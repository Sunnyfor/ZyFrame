package com.sunny.zy.preview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.utils.GlideApp
import kotlinx.android.synthetic.main.zy_item_gallery_preview.view.*

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/28 11:47
 */
class PreviewPhotoAdapter(data: ArrayList<Uri>) : BaseRecycleAdapter<Uri>(data) {

    var selectUri: Uri? = null

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        GlideApp.with(context)
            .load(getData(position))
            .into(holder.itemView.iv_photo)

        if (selectUri == getData(position)) {
            holder.itemView.v_border.visibility = View.VISIBLE
        } else {
            holder.itemView.v_border.visibility = View.GONE
        }
    }

    override fun setLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_preview, parent, false)
    }
}