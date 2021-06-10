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
    private var aspectX = 1
    private var aspectY = 1
    private var outputX = 500
    private var outputY = 500
    private var isCrop = true


    private val scope: CoroutineScope by lazy {
        MainScope()
    }

    /**
     * 跳转相机
     */
    fun startCamera(activity: BaseActivity, fileName: String = "") {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            initFile(fileName)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)// 更改系统默认存储路径
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    file?.delete()
                    return@registerForActivityResult
                }
                if (isCrop) {
                    startPhotoZoom(activity, null)
                } else {
                    onResultListener?.onResult(file ?: return@registerForActivityResult)
                }
            }.launch(intent)
        }
    }


    /**
     * 跳转相册
     */
    fun startAlbum(activity: BaseActivity, fileName: String = "") {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            initFile(fileName)
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    file?.delete()
                    return@registerForActivityResult
                }
                it.data?.data?.let { mUri ->
                    if (isCrop) {
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
    private fun startPhotoZoom(activity: BaseActivity, mUri: Uri?) {

        val intent = Intent("com.android.camera.action.CROP")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true")
        intent.putExtra("scale", true)
        intent.setDataAndType(mUri ?: uri, "image/*")
        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", outputX)
        intent.putExtra("outputY", outputY)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("return-data", false)
        intent.putExtra("noFaceDetection", true) //

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            FileUtil.getPicturesUri(file?.name ?: "")?.let {
                uri = it
                if (mUri == null) {
                    intent.setDataAndType(it, "image/*")
                }
                activity.contentResolver.openOutputStream(it)?.use { os ->
                    os.write(file?.readBytes())
                }
                file?.delete()
                file?.createNewFile()
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        }

        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                file?.delete()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    uri?.let { uri ->
                        activity.contentResolver.delete(uri, null)
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    uri?.let { uri ->
                        activity.contentResolver.openInputStream(uri)
                            ?.use { inputSteam ->
                                file?.delete()
                                file?.createNewFile()
                                file?.writeBytes(inputSteam.readBytes())
                                activity.contentResolver.delete(uri, null)
                            }
                    }
                }
                onResultListener?.onResult(file ?: return@registerForActivityResult)
            }
        }.launch(intent)

    }

    private fun whiteResult(activity: BaseActivity, uri: Uri) {
        val inputStream = activity.contentResolver.openInputStream(uri)
        scope.launch {
            activity.showLoading()
            withContext(IO) {
                file?.writeBytes(inputStream?.readBytes() ?: return@withContext)
            }
            activity.hideLoading()
            onResultListener?.onResult(file ?: return@launch)

        }
    }


    fun setAspectXY(aspectX: Int, aspectY: Int) {
        this.aspectX = aspectX
        this.aspectY = aspectY
    }


    fun setOutputXY(outputX: Int, outputY: Int) {
        this.outputX = outputX
        this.outputY = outputY
    }


    fun isCrop(isCrop: Boolean) {
        this.isCrop = isCrop
    }


    //裁剪结果回调
    interface OnResultListener {
        fun onResult(file: File)
    }


    //初始化图片文件及URI
    private fun initFile(fileName: String) {
        //有权限
        file = if (fileName.isEmpty()) {
            FileUtil.getFile("IMG_${System.currentTimeMillis()}.jpg")
        } else {
            val fileNameSb = StringBuilder()
            if (fileName.contains(".")) {
                val result = fileName.split(".")
                fileNameSb.append(result[0])
            }
            fileNameSb.append(".jpg")
            FileUtil.getFile(fileNameSb.toString())
        }

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