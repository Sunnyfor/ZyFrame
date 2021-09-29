package com.sunny.zy.preview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.documentfile.provider.DocumentFile
import com.github.chrisbanes.photoview.PhotoView
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.preview.VideoPreViewActivity
import com.sunny.zy.utils.GlideApp
import kotlinx.android.synthetic.main.zy_layout_videoview.view.*


/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/27 15:43
 */
class PhotoPreviewPageAdapter(data: ArrayList<Uri>) : BaseRecycleAdapter<Uri>(data) {

    var onPhotoCallback: (() -> Unit)? = null

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {

        when (holder.itemView) {
            is PhotoView -> {
                GlideApp.with(context)
                    .load(getData(position))
                    .into(holder.itemView)
            }
            is ConstraintLayout -> {
                GlideApp.with(context)
                    .load(getData(position))
                    .into(holder.itemView.iv_photo)
                holder.itemView.v_play.setOnClickListener {
                    VideoPreViewActivity.intent(context, getData(position))
                }
            }
        }
        holder.itemView.setOnClickListener {
            onPhotoCallback?.invoke()
        }
    }

    override fun setLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(viewType, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        val type =
            DocumentFile.fromSingleUri(ZyFrameStore.getContext(), getData(position))?.type ?: ""
        if (type.contains("video")) {
            return R.layout.zy_layout_videoview
        }
        return R.layout.zy_layout_photoview
    }

}