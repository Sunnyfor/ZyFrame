package com.sunny.zy.gallery.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sunny.zy.R
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder
import com.sunny.zy.gallery.GallerySelectActivity
import com.sunny.zy.gallery.bean.GalleryBean
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


    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {

        val data = getData(position)
        Glide.with(context)
            .load(data.uri)
            .into(holder.getView(R.id.ivGalleryPhoto))

        val tvSelect = holder.getView<TextView>(R.id.tvSelect)
        val vMask = holder.getView<View>(R.id.vMask)

        when (selectType) {
            GallerySelectActivity.SELECT_TYPE_SINGLE -> {
                tvSelect.visibility = View.GONE
            }
            GallerySelectActivity.SELECT_TYPE_MULTIPLE -> {
                tvSelect.visibility = View.VISIBLE
                if (selectList.contains(data)) {
                    vMask.setBackgroundColor(Color.parseColor("#3f000000"))
                    tvSelect.setBackgroundResource(R.drawable.svg_gallery_content_select_number)
                    val selectStr = (selectList.indexOf(data) + 1).toString()
                    tvSelect.text = selectStr
                } else {
                    vMask.setBackgroundColor(Color.parseColor("#15000000"))
                    tvSelect.text = null
                    tvSelect.setBackgroundResource(R.drawable.svg_gallery_content_unselect)
                }
            }
        }

        val vPlay = holder.getView<View>(R.id.vPlay)
        val tvDuration = holder.getView<TextView>(R.id.tvDuration)
        val flSelect = holder.getView<FrameLayout>(R.id.flSelect)

        if (data.type.contains("video")) {
            vPlay.visibility = View.VISIBLE
            val mm: String = DecimalFormat("00").format(data.duration / 1000 % 3600 / 60)
            val ss: String = DecimalFormat("00").format(data.duration / 1000 % 60)
            val durationStr = "$mm:$ss"
            tvDuration.text = durationStr
        } else {
            vPlay.visibility = View.GONE
        }
        flSelect.setOnClickListener {
            selectCallback?.invoke(position)
        }
        tvDuration.visibility = vPlay.visibility
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_content, parent, false)
    }
}