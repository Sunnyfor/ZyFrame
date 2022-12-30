package com.sunny.zy.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunny.kit.utils.FileUtil
import com.sunny.kit.utils.StringUtil
import com.sunny.kit.utils.ToastUtil
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.callback.GalleryPreviewCallback
import com.sunny.zy.gallery.adapter.GalleryContentAdapter
import com.sunny.zy.gallery.adapter.GalleryFolderAdapter
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.gallery.bean.GalleryFolderBean
import com.sunny.zy.gallery.contract.GalleryContract
import com.sunny.zy.utils.*
import com.sunny.zy.utils.permission.PermissionResult
import com.sunny.zy.utils.permission.PermissionUtil
import java.io.File

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 17:12
 */
class GallerySelectActivity : BaseActivity(), GalleryContract.IView, GalleryPreviewCallback,
    PermissionResult {

    private val galleryResultList = arrayListOf<GalleryBean>()

    private val folderAdapter = GalleryFolderAdapter()

    private val contentAdapter = GalleryContentAdapter(galleryResultList)

    private var selectType = SELECT_TYPE_MULTIPLE

    private var maxSize = 8

    private var aspectX = 0
    private var aspectY = 0

    private var fileType = FILE_TYPE_ALL

    private var isCrop = false

    private val presenter: GalleryContract.Presenter by lazy {
        GalleryContract.Presenter(this)
    }

    private val permissionUtil by lazy {
        PermissionUtil(this)
    }

    companion object {
        const val SELECT_TYPE_INT = "selectType"
        const val SELECT_TYPE_MULTIPLE = 0  //多选
        const val SELECT_TYPE_SINGLE = 1  //单选
        const val MAX_SIZE_INT = "maxSize"
        const val IS_CROP_BOOLEAN = "isCrop"
        const val ASPECT_X_INT = "aspectX"
        const val ASPECT_Y_INT = "aspectY"
        const val FILE_TYPE_INT = "fileType"
        const val FILE_TYPE_ALL = 0
        const val File_TYPE_IMAGE = 1
        const val File_TYPE_VIDEO = 2
    }


    private val rvFolder by lazy {
        findViewById<RecyclerView>(R.id.rvFolder)
    }

    private val vBg by lazy {
        findViewById<View>(R.id.vBg)
    }

    private val rvContent by lazy {
        findViewById<RecyclerView>(R.id.rvContent)
    }

    private val galleryIntent by lazy {
        GalleryIntent()
    }

    override fun initLayout() = R.layout.zy_act_photo_select


    @Suppress("UNCHECKED_CAST")
    override fun initView() {
        //初始化标题栏
        toolbar.setTitleCustom(R.layout.zy_layout_title_photo_select)
        setImmersionResource(R.color.preview_bg)
        toolbar.getView<ImageView>(R.id.ivBack)?.setOnClickListener(this)
        toolbar.getView<ConstraintLayout>(R.id.clTitle)?.setOnClickListener(this)
        toolbar.getView<TextView>(R.id.tvComplete)?.setOnClickListener(this)

        galleryIntent.onCreate(this)

        intent.getBundleExtra("flags")?.let {
            selectType = it.getInt(SELECT_TYPE_INT, SELECT_TYPE_MULTIPLE)
            fileType = it.getInt(FILE_TYPE_INT, FILE_TYPE_ALL)
            isCrop = it.getBoolean(IS_CROP_BOOLEAN, false)
            aspectX = it.getInt(ASPECT_X_INT, 0)
            aspectY = it.getInt(ASPECT_Y_INT, 0)
            maxSize = it.getInt(MAX_SIZE_INT, 8)
        }

        contentAdapter.selectType = selectType

        rvFolder.layoutManager = LinearLayoutManager(this)
        folderAdapter.setOnItemClickListener { _, position ->
            val lastPosition = folderAdapter.selectIndex
            folderAdapter.selectIndex = position
            folderAdapter.notifyItemChanged(lastPosition)
            folderAdapter.notifyItemChanged(position)
            updateTitle(position)
            toggleGallery()
        }

        rvContent.itemAnimator = null
        rvContent.layoutManager = GridLayoutManager(this, 4)
        rvContent.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val margin = resources.getDimension(R.dimen.dp_1).toInt()
                val position = parent.getChildAdapterPosition(view)
                if (position % 4 != 0) {
                    outRect.left = margin
                }
                outRect.bottom = margin

            }
        })

        contentAdapter.selectCallback = { position ->
            val data = contentAdapter.getData(position)
            if (galleryResultList.contains(data)) {
                galleryResultList.remove(data)
                contentAdapter.notifyItemChanged(position)
                contentAdapter.getData().forEachIndexed { index, galleryContentBean ->
                    galleryResultList.forEach {
                        if (it == galleryContentBean) {
                            contentAdapter.notifyItemChanged(index)
                        }
                    }
                }
                updateCount()
            } else {
                if (galleryResultList.size < maxSize) {
                    galleryResultList.add(data)
                    updateCount()
                    contentAdapter.notifyItemChanged(position)
                } else {
                    ToastUtil.show(
                        String.format(
                            getString(
                                R.string.maxSizeHint,
                                maxSize.toString()
                            )
                        )
                    )
                }
            }
        }

        contentAdapter.setOnItemClickListener { _, position ->

            val data = contentAdapter.getData(position)

            if (selectType == SELECT_TYPE_SINGLE) {
                if (data.type.contains("image") && isCrop) {
                    intentCrop(data)
                } else {
                    galleryResultList.add(data)
                    setResult()
                }
                return@setOnItemClickListener
            }

            val dataList = contentAdapter.getData()

            galleryIntent.startGallerySelectPreview(
                dataList,
                galleryResultList,
                position,
                maxSize,
                this
            )
        }
    }

    override fun loadData() {
        permissionUtil.requestPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


    private fun updateTitle(position: Int) {
        toolbar.findViewById<TextView>(R.id.tvTitleGalleryName)?.text =
            folderAdapter.getData(position).name
        contentAdapter.getData().clear()
        contentAdapter.getData().addAll(folderAdapter.getData(position).list)
        rvContent.adapter = contentAdapter
    }

    private fun updateCount() {
        val completeText = toolbar.findViewById<TextView>(R.id.tvComplete)
        val textSb = StringBuilder()
        textSb.append(getString(R.string.complete))

        if (galleryResultList.isNotEmpty()) {
            completeText?.setBackgroundResource(R.drawable.sel_title_btn_bg)
            completeText?.setTextColor(ContextCompat.getColor(this, R.color.color_white))
            textSb.append("(").append(galleryResultList.size).append("/").append(maxSize)
                .append(")")
        } else {
            completeText?.setBackgroundResource(R.drawable.sel_title_btn_bg_unenable)
            completeText?.setTextColor(ContextCompat.getColor(this, R.color.font_gray))
        }
        completeText?.text = textSb.toString()
    }


    private fun toggleGallery() {

        val expandAnim: Animation

        if (rvFolder.visibility == View.VISIBLE) {
            rvFolder.visibility = View.GONE
            expandAnim = AnimationUtils.loadAnimation(this, R.anim.gallery_folder_collect)
        } else {
            rvFolder.visibility = View.VISIBLE
            expandAnim = AnimationUtils.loadAnimation(this, R.anim.gallery_folder_expand)
        }

        toolbar.findViewById<View>(R.id.ivExpand)?.startAnimation(expandAnim)

        if (rvFolder.visibility == View.GONE) {
            rvFolder.animation =
                AnimationUtils.loadAnimation(this, R.anim.gallery_folder_out).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            vBg.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}

                    })
                }
        } else {
            rvFolder.animation =
                AnimationUtils.loadAnimation(this, R.anim.gallery_folder_in).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            vBg.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                }
        }
    }


    override fun onClickEvent(view: View) {
        when (view.id) {
            R.id.ivBack -> finish()

            R.id.clTitle -> {
                toggleGallery()
            }
            R.id.tvComplete -> {
                if (galleryResultList.isEmpty()) {
                    return
                }
                setResult()
            }
        }
    }

    private fun setResult() {
        val resultIntent = Intent()
        resultIntent.putExtra("data", galleryResultList)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onClose() {}

    override fun showGalleryData(data: List<GalleryFolderBean>) {
        folderAdapter.getData().clear()
        folderAdapter.getData().addAll(data)
        rvFolder.adapter = folderAdapter

        if (folderAdapter.getData().isNotEmpty()) {
            updateTitle(0)
        }
    }

    private fun intentCrop(data: GalleryBean) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true")
        intent.putExtra("scaleUpIfNeeded", true)
        intent.putExtra("scale", true)
        intent.setDataAndType(data.uri, "image/*")
        if (aspectX != 0) {
            intent.putExtra("aspectX", aspectX)
        }

        if (aspectY != 0) {
            intent.putExtra("aspectY", aspectY)
        }

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("return-data", false)
        intent.putExtra("noFaceDetection", true) //

        val outFile = File(FileUtil.getExternalDir(), StringUtil.getTimeStamp() + ".jpg")
        val outUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            FileUtil.insertImage(outFile.name)
        } else {
            Uri.fromFile(outFile)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                LogUtil.i("裁剪成功")
//                FileUtil.cropResultGetUri(outUri, outFile)?.let { resultUri ->
//                    outUri = resultUri
//                }
//                outUri?.let { uri ->
//                    data.uri = uri
//                }
//                galleryResultList.add(data)
//                IntentManager.selectResultCallBack?.invoke(galleryResultList)
//                finish()
//            } else {
//                LogUtil.i("裁剪失败")
//                LogUtil.i("uri:$outUri")
//            }
//        }.launch(intent)
    }


    override fun onPermissionFailed(failedPermissions: List<String>) {}

    override fun onPermissionSuccess(successPermissions: List<String>) {
        when (fileType) {
            FILE_TYPE_ALL -> presenter.loadImageAndVideData()
            File_TYPE_IMAGE -> presenter.loadImageData()
            File_TYPE_VIDEO -> presenter.loadVideoData()
        }
    }

    /**
     * 预览结果处理
     */
    override fun onResult(flag: Boolean, resultList: ArrayList<GalleryBean>) {
        galleryResultList.clear()
        galleryResultList.addAll(resultList)

        if (flag) {
            setResult()
        } else {
            contentAdapter.notifyItemRangeChanged(0, contentAdapter.itemCount)
            updateCount()
        }
    }
}