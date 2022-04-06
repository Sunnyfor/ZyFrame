package com.sunny.zy.preview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.preview.adapter.PhotoPreviewPageAdapter
import com.sunny.zy.preview.adapter.PreviewPhotoAdapter
import com.sunny.zy.utils.IntentManager
import com.sunny.zy.utils.ToastUtil
import kotlinx.android.synthetic.main.zy_act_preview_photo.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/26 19:11
 */
class GalleryPreviewActivity : BaseActivity() {

    companion object {
        const val TYPE_SELECT = 0
        const val TYPE_PREVIEW = 1
        const val TYPE_CAMERA = 2
    }


    private val deleteList = arrayListOf<GalleryBean>()

    private val dataList = arrayListOf<GalleryBean>()

    private val selectList = arrayListOf<GalleryBean>()

    private val type by lazy {
        intent.getIntExtra("type", TYPE_SELECT)
    }

    private val isDelete by lazy {
        intent.getBooleanExtra("isDelete", false)
    }

    private val previewAdapter: PreviewPhotoAdapter by lazy {
        PreviewPhotoAdapter(selectList).apply {
            setOnItemClickListener { _, position ->
                val index = dataList.indexOf(getData(position))
                viewPager.currentItem = index
            }
        }
    }

    private var index = 0

    private var maxSize = 0

    override fun initLayout() = R.layout.zy_act_preview_photo

    override fun initView() {

        setStatusBarColor(R.color.preview_bg)

        index = intent.getIntExtra("index", 0)
        maxSize = intent.getIntExtra("maxSize", 0)
        dataList.addAll(ZyFrameStore.getData<ArrayList<GalleryBean>>("dataList",true)?: arrayListOf())
        selectList.addAll(ZyFrameStore.getData<ArrayList<GalleryBean>>("selectList",true)?: arrayListOf())

        updateTitle()

        when (type) {
            TYPE_CAMERA -> {
                tv_complete.visibility = View.VISIBLE
            }

            TYPE_PREVIEW -> {
                if (isDelete) {
                    iv_delete.visibility = View.VISIBLE
                }
            }
            TYPE_SELECT -> {
                tv_complete.visibility = View.VISIBLE
                cl_select.visibility = View.VISIBLE
                rv_preview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                rv_preview.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        when (parent.getChildAdapterPosition(view)) {
                            0 -> {
                                outRect.left = resources.getDimension(R.dimen.dp_8).toInt()
                            }
                            selectList.size - 1 -> {
                                outRect.right = resources.getDimension(R.dimen.dp_8).toInt()
                                outRect.left = resources.getDimension(R.dimen.dp_16).toInt()
                            }
                            else -> {
                                outRect.left = resources.getDimension(R.dimen.dp_16).toInt()
                            }
                        }
                    }
                })

                previewAdapter.selectBean = dataList[index]
                rv_preview.adapter = previewAdapter
                updateChecked()
            }
        }


        viewPager.adapter = PhotoPreviewPageAdapter(dataList).apply {
            onPhotoCallback = {
                if (cl_title.visibility == View.VISIBLE) {
                    hideStatusBar(false)
                    cl_title.visibility = View.GONE

                    if (type == TYPE_SELECT) {
                        cl_preview.visibility = View.GONE
                        cl_select.visibility = View.GONE
                    }

                } else {
                    showStatusBar()
                    cl_title.visibility = View.VISIBLE
                    if (type == TYPE_SELECT) {
                        if (selectList.isNotEmpty()) {
                            cl_preview.visibility = View.VISIBLE
                        }
                        cl_select.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPageSelected(position: Int) {
                index = position
                if (type == TYPE_SELECT) {
                    previewAdapter.selectBean = dataList[index]
                    previewAdapter.notifyDataSetChanged()
                }
                updateTitle()
                updateChecked()
            }
        })

        viewPager.setCurrentItem(index, false)

        setOnClickListener(cl_title, iv_back, tv_complete, iv_delete, iv_select, tv_select)

    }

    override fun loadData() {

    }


    override fun onClickEvent(view: View) {
        when (view.id) {
            R.id.iv_back -> setResult()
            R.id.cl_title -> {
            }

            R.id.tv_complete -> {

                if (type == TYPE_CAMERA) {
                    setResult(true)
                    return
                }
                if (selectList.isEmpty()) {
                    selectList.add(dataList[index])
                    updateTitle()
                    updateChecked()
                    previewAdapter.notifyItemInserted(0)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    setResult(true)
                }, 100)
            }

            R.id.iv_delete -> {
                deleteList.add(dataList[index])
                dataList.removeAt(index)
                if (dataList.isEmpty()) {
                    setResult()
                } else {
                    viewPager.adapter?.notifyItemRemoved(index)
                    viewPager.adapter?.notifyItemRangeChanged(index, dataList.size)
                    updateTitle()
                }
            }
            R.id.iv_select, R.id.tv_select -> {
                if (!selectList.contains(dataList[index])) {

                    if (selectList.size >= maxSize) {
                        ToastUtil.show(
                            String.format(
                                getString(
                                    R.string.maxSizeHint,
                                    maxSize.toString()
                                )
                            )
                        )
                        return
                    }
                    selectList.add(dataList[index])
                    previewAdapter.notifyItemInserted(selectList.size - 1)
                } else {
                    val index = selectList.indexOf(dataList[index])
                    selectList.removeAt(index)
                    previewAdapter.notifyItemRemoved(index)
                    previewAdapter.notifyItemRangeChanged(index, selectList.size)
                }
                updateTitle()
                updateChecked()
            }
        }
    }

    private fun setResult(flag: Boolean = false) {
        val intent = Intent()
        intent.putExtra("type", type)
        when (type) {
            TYPE_CAMERA -> IntentManager.previewResultCallBackCallBack?.onCamera(flag)
            TYPE_PREVIEW -> IntentManager.previewResultCallBackCallBack?.onPreview(deleteList)
            TYPE_SELECT -> IntentManager.previewResultCallBackCallBack?.onSelect(selectList, flag)
        }
        IntentManager.previewResultCallBackCallBack = null
        finish()
    }

    override fun onBackPressed() {
        setResult()
    }

    override fun onClose() {

    }

    fun updateTitle() {
        tv_title.text = ("${index + 1}/${dataList.size}")
        if (selectList.isNotEmpty()) {
            tv_complete.text = ("${getString(R.string.complete)}(${selectList.size}/$maxSize)")
        } else {
            tv_complete.text = getString(R.string.complete)
        }
    }

    fun updateChecked() {
        if (selectList.isEmpty()) {
            cl_preview.visibility = View.GONE
        } else {
            cl_preview.visibility = View.VISIBLE
        }

        if (!selectList.contains(dataList[index])) {
            iv_select.setImageResource(R.drawable.svg_gallery_content_unselect)
        } else {
            iv_select.setImageResource(R.drawable.svg_gallery_content_select)
            val index = selectList.indexOf(dataList[index])
            rv_preview.scrollToPosition(index)
        }
    }

    interface OnPreviewResultCallBack {
        fun onPreview(deleteList: ArrayList<GalleryBean>)

        fun onSelect(resultList: ArrayList<GalleryBean>, isFinish: Boolean)

        fun onCamera(isComplete: Boolean)
    }
}