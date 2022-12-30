package com.sunny.zy.preview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sunny.kit.utils.DensityUtil
import com.sunny.kit.utils.ToastUtil
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.preview.adapter.PhotoPreviewPageAdapter
import com.sunny.zy.preview.adapter.PreviewPhotoAdapter


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

    private val vStatusBar by lazy {
        findViewById<View>(R.id.statusBar)
    }

    private val clTitle by lazy {
        findViewById<ConstraintLayout>(R.id.clTitle)
    }

    private val viewPager by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
    }

    private val tvTitle by lazy {
        findViewById<TextView>(R.id.tvTitle)

    }

    private val ivBack by lazy {
        findViewById<ImageView>(R.id.ivBack)
    }

    private val tvComplete by lazy {
        findViewById<TextView>(R.id.tvComplete)

    }

    private val ivDelete by lazy {
        findViewById<ImageView>(R.id.ivDelete)
    }

    private val clPreview by lazy {
        findViewById<ConstraintLayout>(R.id.clPreview)
    }

    private val rvPreview by lazy {
        findViewById<RecyclerView>(R.id.rvPreview)
    }

    private val clSelect by lazy {
        findViewById<ConstraintLayout>(R.id.clSelect)
    }

    private val ivSelect by lazy {
        findViewById<ImageView>(R.id.ivSelect)
    }

    private val tvSelect by lazy {
        findViewById<TextView>(R.id.tvSelect)
    }


    private val previewAdapter: PreviewPhotoAdapter by lazy {
        PreviewPhotoAdapter(selectList).apply {
            setOnItemClickListener { _, position ->
                val index = dataList.indexOf(getData(position))
                viewPager.setCurrentItem(index,false)
            }
        }
    }

    private var index = 0

    private var maxSize = 0

    override fun initLayout() = R.layout.zy_act_preview_photo

    override fun initView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        hideStatusBar()
        vStatusBar.layoutParams.height = DensityUtil.getStatusBarHeight()
        index = intent.getIntExtra("index", 0)
        maxSize = intent.getIntExtra("maxSize", 0)
        dataList.addAll(
            intent.getParcelableArrayListExtra("dataList") ?: arrayListOf()
        )
        selectList.addAll(
            intent.getParcelableArrayListExtra("selectList") ?: arrayListOf()
        )

        updateTitle()

        when (type) {
            TYPE_CAMERA -> {
                tvComplete.visibility = View.VISIBLE
            }

            TYPE_PREVIEW -> {
                if (isDelete) {
                    ivDelete.visibility = View.VISIBLE
                }
            }
            TYPE_SELECT -> {
                tvComplete.visibility = View.VISIBLE
                clSelect.visibility = View.VISIBLE
                rvPreview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                rvPreview.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                rvPreview.adapter = previewAdapter
                updateChecked()
            }
        }


        viewPager.adapter = PhotoPreviewPageAdapter(dataList).apply {
            onPhotoCallback = {
                if (clTitle.visibility == View.VISIBLE) {
                    clTitle.visibility = View.GONE
                    vStatusBar.visibility = View.GONE
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    if (type == TYPE_SELECT) {
                        clPreview.visibility = View.GONE
                        clSelect.visibility = View.GONE
                    }

                } else {
                    clTitle.visibility = View.VISIBLE
                    vStatusBar.visibility = View.VISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    if (type == TYPE_SELECT) {
                        if (selectList.isNotEmpty()) {
                            clPreview.visibility = View.VISIBLE
                        }
                        clSelect.visibility = View.VISIBLE
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

        setOnClickListener(clTitle, ivBack, tvComplete, ivDelete, clSelect, ivSelect, tvSelect)

    }

    override fun loadData() {

    }


    override fun onClickEvent(view: View) {
        when (view.id) {
            R.id.ivBack -> setResult()
            R.id.clTitle,R.id.clSelect -> {}
            R.id.tvComplete -> {

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

            R.id.ivDelete -> {
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
            R.id.ivSelect, R.id.tvSelect -> {
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
        intent.putExtra("flag", flag)
        intent.putExtra("data", selectList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        setResult()
    }

    override fun onClose() {

    }

    fun updateTitle() {
        val titleStr = "${index + 1}/${dataList.size}"
        tvTitle.text = titleStr
        if (selectList.isNotEmpty()) {
            val completeStr = "${getString(R.string.complete)}(${selectList.size}/$maxSize)"
            tvComplete.text = completeStr
        } else {
            tvComplete.text = getString(R.string.complete)
        }
    }

    fun updateChecked() {
        if (selectList.isEmpty()) {
            clPreview.visibility = View.GONE
        } else {
            if (clTitle.visibility == View.VISIBLE) {
                clPreview.visibility = View.VISIBLE
            }
        }

        if (!selectList.contains(dataList[index])) {
            ivSelect.setImageResource(R.drawable.svg_gallery_content_unselect)
        } else {
            ivSelect.setImageResource(R.drawable.svg_gallery_content_select)
            val index = selectList.indexOf(dataList[index])
            rvPreview.scrollToPosition(index)
        }
    }
}