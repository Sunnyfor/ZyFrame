package com.sunny.zy.preview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.chrisbanes.photoview.PhotoView
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.preview.VideoPlayActivity
import com.sunny.zy.utils.GlideApp
import kotlinx.android.synthetic.main.zy_layout_videoview.view.*


/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/27 15:43
 */
class PhotoPreviewPageAdapter(data: ArrayList<GalleryBean>) :
    BaseRecycleAdapter<GalleryBean>(data) {

    var onPhotoCallback: (() -> Unit)? = null

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {

        when (holder.itemView) {
            is PhotoView -> {
                GlideApp.with(context)
                    .load(getData(position).uri)
                    .into(holder.itemView)
            }
            is ConstraintLayout -> {
                GlideApp.with(context)
                    .load(getData(position).uri)
                    .into(holder.itemView.iv_photo)
                holder.itemView.v_play.setOnClickListener {
                    VideoPlayActivity.intent(
                        context as BaseActivity,
                        getData(position).uri ?: return@setOnClickListener,
                        null
                    )
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
        if (getData(position).type.contains("video")) {
            return R.layout.zy_layout_videoview
        }
        return R.layout.zy_layout_photoview
    }
}