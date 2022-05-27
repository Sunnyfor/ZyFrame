package com.sunny.zy.utils

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.ZyFrameConfig
import java.io.File
import java.text.DecimalFormat

/**
 * 文件操作相关工具类
 */
object FileUtil {

    /**
     * 获取换成你文件路径
     */
    private fun getCacheDir(): File? {
        return File(ZyFrameConfig.TEMP).parentFile
    }


    /**
     * 根据文件名获取文件
     */
    fun getFile(name: String): File {
        return File(getCacheDir(), name)
    }


    fun insertImage(name: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        return ZyFrameStore.getContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }


    fun cropResultGetUri(uri: Uri?, file: File): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && uri != null) {
            ZyFrameStore.getContext().contentResolver.openInputStream(uri)?.use {
                if (file.exists()) {
                    file.delete()
                }
                file.createNewFile()
                file.writeBytes(it.readBytes())
                ZyFrameStore.getContext().contentResolver.delete(uri, null)
                return FileProvider.getUriForFile(
                    ZyFrameStore.getContext(),
                    ZyFrameConfig.authorities,
                    file
                )
            }
        }
        return null
    }


    /**
     * 获取目录缓存大小
     */
    fun getCacheSize(): Long {
        var size = 0L
        size += getFolderSize(getCacheDir())
        return size
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    fun formatFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        return when {
            fileS < 1024 -> df.format(fileS.toDouble()) + "B"
            fileS < 1048576 -> df.format(fileS.toDouble() / 1024) + "KB"
            fileS < 1073741824 -> df.format(fileS.toDouble() / 1048576) + "MB"
            else -> df.format(fileS.toDouble() / 1073741824) + "GB"
        }
    }


    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private fun getFolderSize(file: File?): Long {
        var size: Long = 0

        if (file == null) {
            return size
        }
        try {
            file.listFiles()?.let {
                for (aFileList in it) {
                    size += if (aFileList.isDirectory) {
                        getFolderSize(aFileList)
                    } else {
                        aFileList.length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }


    /**
     * 删除所有缓存文件
     */
    fun deleteAllFile() {
        getCacheDir()?.listFiles()?.forEach {
            deleteFolderFile(it.path)
        }
    }

    private fun deleteFolderFile(filePath: String) {
        val file = File(filePath)
        if (file.isDirectory) {
            val files = file.listFiles()
            files?.forEach {
                deleteFolderFile(it.absolutePath)
            }
        } else {
            file.delete()
        }
    }

    /**
     * 从一个文件路径得到URI
     */
    fun getUriFromPath(path: String): Uri? {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    ZyFrameStore.getContext(), ZyFrameConfig.authorities, File(path)
                )
            } else {
                Uri.fromFile(File(path))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}