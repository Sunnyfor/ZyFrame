package com.sunny.zy.gallery.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.GallerySelectActivity
import com.sunny.zy.gallery.bean.GalleryBean
import kotlinx.android.synthetic.main.zy_item_gallery_content.view.*
import java.text.DecimalFormat

/**
 * Desc 相册
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/23
 */
class GalleryContentAdapter(private val selectList: ArrayList<GalleryBean>) :
    BaseRecycleAdapter<GalleryBean>(arrayListOf()) {

    var selectType = GallerySelectActivity.SELECT_TYPE_MULTIPLE

    var selectCallback: ((position: Int) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        val data = getData(position)
        Glide.with(context)
            .load(data.uri)
            .into(holder.itemView.iv_gallery_photo)

        when (selectType) {
            GallerySelectActivity.SELECT_TYPE_SINGLE -> {
                holder.itemView.tv_select.visibility = View.GONE
            }
            GallerySelectActivity.SELECT_TYPE_MULTIPLE -> {
                holder.itemView.tv_select.visibility = View.VISIBLE
                if (selectList.contains(data)) {
                    holder.itemView.v_mask.setBackgroundColor(Color.parseColor("#3f000000"))
                    holder.itemView.tv_select.setBackgroundResource(R.drawable.svg_gallery_content_select_number)
                    holder.itemView.tv_select.text = (selectList.indexOf(data) + 1).toString()
                } else {
                    holder.itemView.v_mask.setBackgroundColor(Color.parseColor("#15000000"))
                    holder.itemView.tv_select.text = null
                    holder.itemView.tv_select.setBackgroundResource(R.drawable.svg_gallery_content_unselect)
                }
            }
        }

        if (data.type.contains("video")) {
            holder.itemView.v_play.visibility = View.VISIBLE
            val mm: String = DecimalFormat("00").format(data.duration / 1000 % 3600 / 60)
            val ss: String = DecimalFormat("00").format(data.duration / 1000 % 60)
            holder.itemView.tv_duration.text = ("$mm:$ss")
        } else {
            holder.itemView.v_play.visibility = View.GONE
        }

        holder.itemView.fl_select.setOnClickListener {
            selectCallback?.invoke(position)
        }

        holder.itemView.tv_duration.visibility = holder.itemView.v_play.visibility
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_content, parent, false)
    }
}