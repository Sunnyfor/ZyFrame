package com.sunny.zy.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.http.ZyConfig
import java.io.File

/**
 * Desc 相机相册工具类
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
 * Date 2020年3月4日 11:56:34
 */
class CameraUtil(private var activity: BaseActivity) {

    var onResultListener: OnResultListener? = null

    private val photoType = 100 //图片类型
    private var isCrop = false //是否裁剪类型
    private val cameraType = 102 //相机类型
    private var file: File? = null
    private var uri: Uri? = null
    private var type = cameraType
    private var aspectX = 1
    private var aspectY = 1
    private var outputX = 150
    private var outputY = 150

    //跳转相机
    fun intentCamera() {
        try {
            type = cameraType
            initFile()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)// 更改系统默认存储路径
            activity.startActivityForResult(intent, cameraType)
        } catch (e: ActivityNotFoundException) {
            ToastUtil.show("没有检测到摄像头！")
        }
    }


    //跳转相册
    private fun intentPhoto(isCrop: Boolean) {
        try {
            this.isCrop = isCrop
            this.type = photoType
            initFile()
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            activity.startActivityForResult(intent, type)
        } catch (e: ActivityNotFoundException) {
            ToastUtil.show("没有检测到相册！")
        }

    }


    fun intentPhoto() {
        intentPhoto(false)
    }


    fun intentPhotoAndCrop() {
        intentPhoto(true)
    }

    /**
     * 结果回调需要在Activity回调中调用
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                cameraType -> {
                    onResultListener?.onResult(file ?: return)

                }
                photoType -> {
                    if (isCrop) {
                        startPhotoZoom(data?.data ?: return)
                    } else {
                        onResultListener?.onResult(file ?: return)
                    }
                }
            }
        }
    }


    //裁剪图片
    private fun startPhotoZoom(mUri: Uri) {
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        intent.setDataAndType(mUri, "image/*")
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true")
        intent.putExtra("scale", true)

        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", outputX)
        intent.putExtra("outputY", outputY)
        intent.putExtra("return-data", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true) // no face detection
        isCrop = false
        activity.startActivityForResult(intent, cameraType)
    }


    fun setAspectXY(aspectX: Int, aspectY: Int) {
        this.aspectX = aspectX
        this.aspectY = aspectY
    }

    fun setOutputXY(outputX: Int, outputY: Int) {
        this.outputX = outputX
        this.outputY = outputY
    }


    //裁剪结果回调
    interface OnResultListener {
        fun onResult(file: File)
    }


    //初始化图片文件及URI
    private fun initFile() {
        //有权限
        file = File(ZyConfig.TEMP, "IMG_${System.currentTimeMillis()}.jpg")
        file?.let {
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(activity, ZyConfig.authorities, it)
            } else {
                Uri.fromFile(file)
            }
        }

    }
}