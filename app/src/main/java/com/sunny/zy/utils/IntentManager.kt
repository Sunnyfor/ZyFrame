package com.sunny.zy.utils

import android.content.Intent
import android.net.Uri
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.activity.CameraActivity
import com.sunny.zy.activity.QRCodeActivity
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.gallery.GallerySelectActivity
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.preview.GalleryPreviewActivity
import com.sunny.zy.preview.VideoPlayActivity

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/25 18:40
 */
object IntentManager {

    //跳转二维码扫描
    var qrCodeResultCallBack: ((result: String) -> Unit)? = null
    fun startQrCodeActivity(resultCallBack: (result: String) -> Unit) {
        qrCodeResultCallBack = resultCallBack
        getActivity().startActivity(Intent(getActivity(), QRCodeActivity::class.java))
    }

    var cameraResultCallBack: ((bean: GalleryBean) -> Unit)? = null
    fun startCameraActivity(resultCallback: (bean: GalleryBean) -> Unit) {
        cameraResultCallBack = resultCallback
        getActivity().startActivity(Intent(getActivity(), CameraActivity::class.java))
    }


    //跳转相册的
    var selectResultCallBack: ((selectList: ArrayList<GalleryBean>) -> Unit)? = null
    fun startGallerySelectActivity(
        flags: GalleryFlagsBuild? = null,
        resultCallBack: (selectList: ArrayList<GalleryBean>) -> Unit
    ) {
        selectResultCallBack = resultCallBack
        val intent = Intent(getActivity(), GallerySelectActivity::class.java)
        intent.putExtra("flags", flags?.build())
        getActivity().startActivity(intent)
    }

    //跳转预览并选择
    var previewResultCallBackCallBack: GalleryPreviewActivity.OnPreviewResultCallBack? = null
    fun startGalleryPreviewActivity(
        dataList: ArrayList<GalleryBean>,
        selectList: ArrayList<GalleryBean>,
        index: Int = 0,
        maxSize: Int = 0,
        resultCallback: GalleryPreviewActivity.OnPreviewResultCallBack?
    ) {
        previewResultCallBackCallBack = resultCallback
        val intent = Intent(getActivity(), GalleryPreviewActivity::class.java)
        ZyFrameStore.setData("dataList", dataList)
        intent.putExtra("index", index)
        intent.putExtra("maxSize", maxSize)
        intent.putExtra("type", GalleryPreviewActivity.TYPE_SELECT)
        ZyFrameStore.setData("selectList", selectList)
        getActivity().startActivity(intent)
    }

    //仅预览
    fun startGalleryPreviewActivity(
        dataList: ArrayList<GalleryBean>,
        index: Int = 0,
        isDelete: Boolean,
        resultCallback: GalleryPreviewActivity.OnPreviewResultCallBack?
    ) {
        previewResultCallBackCallBack = resultCallback
        val intent = Intent(getActivity(), GalleryPreviewActivity::class.java)
        ZyFrameStore.setData("dataList", dataList)
        intent.putExtra("index", index)
        intent.putExtra("type", GalleryPreviewActivity.TYPE_PREVIEW)
        intent.putExtra("isDelete", isDelete)
        getActivity().startActivity(intent)
    }

    //相机预览
    fun startGalleryPreviewActivity(
        bean: GalleryBean,
        resultCallback: GalleryPreviewActivity.OnPreviewResultCallBack?
    ) {
        previewResultCallBackCallBack = resultCallback
        val intent = Intent(getActivity(), GalleryPreviewActivity::class.java)
        ZyFrameStore.setData("dataList", arrayListOf(bean))
        intent.putExtra("type", GalleryPreviewActivity.TYPE_CAMERA)
        getActivity().startActivity(intent)
    }

    //跳转本地视频预览
    var videoPlayResultCallBack: ((isComplete: Boolean) -> Unit)? = null
    fun startVideoPlayActivity(
        uri: Uri,
        resultCallback: ((isComplete: Boolean) -> Unit)? = null
    ) {
        videoPlayResultCallBack = resultCallback
        val intent = Intent(getActivity(), VideoPlayActivity::class.java)
        intent.putExtra("uri", uri)
        if (resultCallback != null) {
            intent.putExtra("isShowComplete", true)
        }
        getActivity().startActivity(intent)
    }

    //跳转网络视频预览
    fun startVideoPlayActivity(url: String) {
        val intent = Intent(getActivity(), VideoPlayActivity::class.java)
        intent.putExtra("url", url)
        getActivity().startActivity(intent)
    }


    fun getActivity() = ZyFrameStore.getActivity(ZyFrameStore.getActivitySize() - 1)
}