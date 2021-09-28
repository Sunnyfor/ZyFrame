package com.sunny.zy.preview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.preview.adapter.PhotoPreviewPageAdapter
import com.sunny.zy.preview.adapter.PreviewPhotoAdapter
import com.sunny.zy.utils.ToastUtil
import kotlinx.android.synthetic.main.zy_act_preview_photo.*

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/26 19:11
 */
class PhotoPreviewActivity : BaseActivity() {

    companion object {

        const val TYPE_PREVIEW = 0
        const val TYPE_DELETE = 1

        fun intentDelete(
            activity: AppCompatActivity,
            dataList: ArrayList<Uri?>,
            index: Int = 0,
            onResultCallback: (deleteList: ArrayList<Uri>) -> Unit
        ) {
            val intent = Intent(activity, PhotoPreviewActivity::class.java)
            intent.putExtra("dataList", dataList)
            intent.putExtra("index", index)
            intent.putExtra("type", TYPE_DELETE)
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                onResultCallback.invoke(
                    it.data?.getParcelableArrayListExtra("deleteList") ?: arrayListOf()
                )
            }.launch(intent)
        }

        fun intentPreview(
            activity: AppCompatActivity,
            dataList: ArrayList<Uri?>,
            selectList: ArrayList<Uri?>,
            index: Int = 0,
            maxSize: Int = 0,
            onResultCallback: (resultList: ArrayList<Uri>, isFinish: Boolean) -> Unit
        ) {
            val intent = Intent(activity, PhotoPreviewActivity::class.java)
            intent.putExtra("dataList", dataList)
            intent.putExtra("index", index)
            intent.putExtra("maxSize", maxSize)
            intent.putExtra("type", TYPE_PREVIEW)
            intent.putExtra("selectList", selectList)
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                onResultCallback.invoke(
                    it.data?.getParcelableArrayListExtra("resultList") ?: arrayListOf(),
                    it.data?.getBooleanExtra("isFinish", false) ?: false
                )
            }.launch(intent)
        }
    }


    private val deleteList = arrayListOf<Uri>()

    private val dataList = arrayListOf<Uri>()

    private val selectList = arrayListOf<Uri>()

    private val type: Int by lazy {
        intent.getIntExtra("type", TYPE_PREVIEW)
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
        dataList.addAll(intent.getParcelableArrayListExtra("dataList") ?: arrayListOf())
        selectList.addAll(intent.getParcelableArrayListExtra("selectList") ?: arrayListOf())

        updateTitle()

        when (type) {
            TYPE_DELETE -> {
                iv_delete.visibility = View.VISIBLE
            }
            TYPE_PREVIEW -> {
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

                previewAdapter.selectUri = dataList[index]
                rv_preview.adapter = previewAdapter
                updateChecked()
            }
        }

        viewPager.adapter = PhotoPreviewPageAdapter(dataList).apply {
            onPhotoCallback = {
                if (cl_title.visibility == View.VISIBLE) {
                    hideStatusBar(false)
                    cl_title.visibility = View.GONE

                    if (type == TYPE_PREVIEW) {
                        cl_preview.visibility = View.GONE
                        cl_select.visibility = View.GONE
                    }

                } else {
                    showStatusBar()
                    cl_title.visibility = View.VISIBLE
                    if (type == TYPE_PREVIEW) {
                        if (selectList.isNotEmpty()) {
                            cl_preview.visibility = View.VISIBLE
                        }
                        cl_select.visibility = View.VISIBLE
                    }
                }
            }
        }
        viewPager.setCurrentItem(index, false)


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPageSelected(position: Int) {
                index = position
                if (type == TYPE_PREVIEW) {
                    previewAdapter.selectUri = dataList[index]
                    previewAdapter.notifyDataSetChanged()
                }
                updateTitle()
                updateChecked()
            }
        })

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

    private fun setResult(isFinish: Boolean = false) {
        val intent = Intent()
        when (type) {
            TYPE_DELETE -> intent.putParcelableArrayListExtra("deleteList", deleteList)
            TYPE_PREVIEW -> {
                intent.putParcelableArrayListExtra("resultList", selectList)
                intent.putExtra("isFinish", isFinish)
            }
        }
        setResult(Activity.RESULT_OK, intent)
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
}