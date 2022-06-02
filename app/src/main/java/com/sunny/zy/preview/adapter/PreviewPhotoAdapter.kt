package com.sunny.zy.preview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.bean.GalleryBean
import kotlinx.android.synthetic.main.zy_item_gallery_preview.view.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/28 11:47
 */
class PreviewPhotoAdapter(data: ArrayList<GalleryBean>) : BaseRecycleAdapter<GalleryBean>(data) {

    var selectBean: GalleryBean? = null

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        Glide.with(context)
            .load(getData(position).uri)
            .into(holder.itemView.iv_photo)

        if (selectBean == getData(position)) {
            holder.itemView.v_border.visibility = View.VISIBLE
        } else {
            holder.itemView.v_border.visibility = View.GONE
        }
        playViewVisibility(holder.itemView.v_play, getData(position).uri)
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_preview, parent, false)
    }

    private fun playViewVisibility(view: View, uri: Uri?) {
        if (uri == null) {
            return
        }
        val type =
            DocumentFile.fromSingleUri(ZyFrameStore.getContext(), uri)?.type ?: ""
        if (type.contains("video")) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}