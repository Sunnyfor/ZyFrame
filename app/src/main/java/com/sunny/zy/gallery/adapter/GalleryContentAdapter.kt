package com.sunny.zy.gallery.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.GallerySelectActivity
import com.sunny.zy.gallery.bean.GalleryContentBean
import com.sunny.zy.utils.GlideApp
import com.sunny.zy.utils.LogUtil
import kotlinx.android.synthetic.main.zy_item_gallery_content.view.*
import java.text.DecimalFormat

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/23 09:45
 */
class GalleryContentAdapter(private val selectList: ArrayList<GalleryContentBean>) :
    BaseRecycleAdapter<GalleryContentBean>(arrayListOf()) {

    var selectType = GallerySelectActivity.SELECT_TYPE_MULTIPLE

    val selectIndex = arrayListOf<Int>()

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        val data = getData(position)
        GlideApp.with(context)
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
                    holder.itemView.tv_select.setBackgroundResource(R.drawable.svg_gallery_content_select)
                    holder.itemView.tv_select.text = (selectList.indexOf(data) + 1).toString()
                } else {
                    holder.itemView.v_mask.setBackgroundColor(Color.parseColor("#15000000"))
                    holder.itemView.tv_select.text = null
                    holder.itemView.tv_select.setBackgroundResource(R.drawable.svg_gallery_content_unselect)
                }
            }
        }

        if (data.type == 1) {
            holder.itemView.v_play.visibility = View.VISIBLE
            val mm: String = DecimalFormat("00").format(data.duration / 1000 % 3600 / 60)
            val ss: String = DecimalFormat("00").format(data.duration / 1000 % 60)
            holder.itemView.tv_duration.text = ("$mm:$ss")
            LogUtil.i(data.duration.toString())
        } else {
            holder.itemView.v_play.visibility = View.GONE
        }

        holder.itemView.tv_duration.visibility = holder.itemView.v_play.visibility
    }

    override fun setLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_content, parent, false)
    }
}