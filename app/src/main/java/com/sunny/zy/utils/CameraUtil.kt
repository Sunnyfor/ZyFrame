package com.sunny.zy.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.http.ZyConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Desc 相机相册工具类
 * Author Zy
 * Date 2020年3月4日 11:56:34
 */
class CameraUtil {

    var onResultListener: OnResultListener? = null

    private var file: File? = null
    private var uri: Uri? = null
    private var aspectX = 500
    private var aspectY = 500

    private val scope: CoroutineScope by lazy {
        MainScope()
    }

    /**
     * 跳转相机
     */
    fun startCamera(activity: BaseActivity) {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            initFile("jpg")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)// 更改系统默认存储路径
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                if (aspectX != 0 && aspectY != 0) {
                    startPhotoZoom(activity, uri ?: return@registerForActivityResult)
                } else {
                    onResultListener?.onResult(file ?: return@registerForActivityResult)
                }
            }.launch(intent)
        }
    }


    /**
     * 跳转相册
     */
    fun startAlbum(activity: BaseActivity) {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            initFile("jpg")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                it.data?.data?.let { mUri ->
                    if (aspectX != 0 && aspectY != 0) {
                        startPhotoZoom(activity, mUri)
                    } else {
                        whiteResult(activity, mUri)
                    }
                }
            }.launch(intent)
        }
    }


    /**
     * 裁剪图片
     */
    private fun startPhotoZoom(activity: BaseActivity, mUri: Uri) {
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
            if (it.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            onResultListener?.onResult(file ?: return@registerForActivityResult)
        }.launch(intent)

    }


    private fun whiteResult(activity: BaseActivity, uri: Uri) {
        val inputStream = activity.contentResolver.openInputStream(uri)
        scope.launch {
            withContext(IO) {
                file?.writeBytes(inputStream?.readBytes() ?: return@withContext)
            }
            if (file?.length() == 0L) {
                file?.delete()
            } else {
                onResultListener?.onResult(file ?: return@launch)
            }
        }
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
    private fun initFile(type: String) {
        //有权限
        file = FileUtil.getFile("IMG_${System.currentTimeMillis()}.$type")
        file?.let {
            if (it.parentFile?.exists() == false) {
                it.parentFile?.mkdirs()
            }
            if (it.exists()) {
                it.delete()
            }
            it.createNewFile()
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(ZyFrameStore.getContext(), ZyConfig.authorities, it)
            } else {
                Uri.fromFile(file)
            }
        }

    }
}