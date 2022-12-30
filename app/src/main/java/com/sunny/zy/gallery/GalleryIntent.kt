package com.sunny.zy.gallery

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sunny.zy.callback.GalleryPreviewCallback
import com.sunny.zy.callback.GallerySelectCallback
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.preview.GalleryPreviewActivity
import com.sunny.zy.utils.GalleryFlagsBuild
import com.sunny.zy.utils.IntentManager

class GalleryIntent {

    private var launcher: ActivityResultLauncher<Intent>? = null

    /**
     * 相册选择结果回调
     *
     */
    private var resultCallBack: Any? = null

    /**
     * 初始化方法
     */
    fun onCreate(activity: AppCompatActivity) {
        launcher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: ArrayList<GalleryBean> =
                        it.data?.getParcelableArrayListExtra("data") ?: arrayListOf()

                    when (resultCallBack) {
                        is GallerySelectCallback -> {
                            (resultCallBack as GallerySelectCallback).onSelectResult(data)
                        }
                        is GalleryPreviewCallback -> {
                            val flag = it.data?.getBooleanExtra("flag", false) ?: false
                            (resultCallBack as GalleryPreviewCallback).onResult(flag, data)
                        }
                    }
                }
            }
    }


    /**
     * 启动相册选择
     */
    fun startGallerySelect(
        flags: GalleryFlagsBuild? = null,
        selectResultCallBack: GallerySelectCallback
    ) {
        resultCallBack = selectResultCallBack
        val intent = Intent(IntentManager.getActivity(), GallerySelectActivity::class.java)
        intent.putExtra("flags", flags?.build())
        launcher?.launch(intent)
    }

    /**
     * 预览并选择
     */
    fun startGallerySelectPreview(
        dataList: ArrayList<GalleryBean>,
        selectList: ArrayList<GalleryBean>,
        index: Int = 0,
        maxSize: Int = 0,
        previewResultCallback: GalleryPreviewCallback
    ) {
        resultCallBack = previewResultCallback
        val intent = Intent(IntentManager.getActivity(), GalleryPreviewActivity::class.java)
        intent.putExtra("dataList", dataList)
        intent.putExtra("index", index)
        intent.putExtra("maxSize", maxSize)
        intent.putExtra("type", GalleryPreviewActivity.TYPE_SELECT)
        intent.putExtra("selectList", selectList)
        launcher?.launch(intent)
    }

    /**
     * 仅预览
     */
    fun startGalleryPreview(
        dataList: ArrayList<GalleryBean>,
        index: Int = 0,
        isDelete: Boolean,
        previewResultCallback: GalleryPreviewCallback
    ) {
        resultCallBack = previewResultCallback
        val intent = Intent(IntentManager.getActivity(), GalleryPreviewActivity::class.java)
        intent.putExtra("dataList", dataList)
        intent.putExtra("index", index)
        intent.putExtra("type", GalleryPreviewActivity.TYPE_PREVIEW)
        intent.putExtra("isDelete", isDelete)
        launcher?.launch(intent)
    }
}


