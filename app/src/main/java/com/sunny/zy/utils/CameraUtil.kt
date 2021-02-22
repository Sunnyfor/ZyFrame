package com.sunny.zy.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.http.ZyConfig
import java.io.File

/**
 * Desc 相机相册工具类
 * Author Zy
 * Date 2020年3月4日 11:56:34
 */
class CameraUtil(private var activity: BaseActivity) {

    var onResultListener: OnResultListener? = null

    private var messageMap = hashMapOf<String, String>()

    private var file: File? = null
    private var uri: Uri? = null
    private var missPermission = ""
    private var aspectX = 500
    private var aspectY = 500


    init {
        messageMap[Manifest.permission.CAMERA] = "相机权限"
        messageMap[Manifest.permission.READ_EXTERNAL_STORAGE] = "存储卡读取权限"
        messageMap[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "存储卡写入权限"
    }

    fun startCamera() {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            intentCamera()
        }
    }


    fun startAlbum() {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            intentPhoto()
        }
    }


    /**
     * 跳转相机
     */
    private fun intentCamera() {
        initFile()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)// 更改系统默认存储路径
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            startPhotoZoom(uri ?: return@registerForActivityResult)
        }.launch(intent)
    }


    /**
     * 跳转相册
     */
    private fun intentPhoto() {
        initFile()
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            startPhotoZoom(it.data?.data ?: return@registerForActivityResult)
        }.launch(intent)
    }


    /**
     * 裁剪图片
     */
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
        if (aspectX != 0 && aspectY != 0) {
            intent.putExtra("outputX", aspectX)
            intent.putExtra("outputY", aspectY)
        }
        intent.putExtra("return-data", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true) // no face detection
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onResultListener?.onResult(file ?: return@registerForActivityResult)
        }.launch(intent)

    }


    fun setAspectXY(aspectX: Int, aspectY: Int) {
        this.aspectX = aspectX
        this.aspectY = aspectY
    }


    //裁剪结果回调
    interface OnResultListener {
        fun onResult(file: File)
    }


    //初始化图片文件及URI
    private fun initFile() {
        //有权限
        file = FileUtil.getFile("IMG_${System.currentTimeMillis()}.jpg")
        file?.let {
            if (it.parentFile?.exists() == false) {
                it.parentFile?.mkdirs()
            }
            if (!it.exists()) {
                it.createNewFile()
            }
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(activity, ZyConfig.authorities, it)
            } else {
                Uri.fromFile(file)
            }
        }

    }

    //检查所有权限是否拥有
    private fun checkPermission(array: Array<String>): Boolean {
        array.forEach {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missPermission = it
                return false
            }
        }
        return true
    }
}