package com.sunny.zy.gallery.model

import android.content.ContentUris
import android.provider.MediaStore
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.gallery.bean.GalleryFolderBean
import java.util.*
import kotlin.collections.ArrayList

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 17:59
 */
class GalleryModel {

    fun getImageAndVideFolder(): ArrayList<GalleryFolderBean> {
        val folderList = ArrayList<GalleryFolderBean>()

        val imageFolderList = getImageFolder()
        if (imageFolderList.isNotEmpty()) {
            folderList.add(imageFolderList[0])
        }

        val videoFolderList = getVideoFolder()
        if (videoFolderList.isNotEmpty()) {
            folderList.add(videoFolderList[0])
        }


        val startList: ArrayList<GalleryFolderBean>
        val endList: ArrayList<GalleryFolderBean>

        val size = if (imageFolderList.size > videoFolderList.size) {
            startList = imageFolderList
            endList = videoFolderList
            startList.size

        } else {
            startList = videoFolderList
            endList = imageFolderList
            endList.size
        }

        for (i in 1 until size) {
            if (endList.size > i) {
                if (startList[i].dateToken > endList[i].dateToken) {
                    folderList.add(startList[i])
                    folderList.add(endList[i])
                } else {
                    folderList.add(endList[i])
                    folderList.add(startList[i])
                }
            } else {
                folderList.add(startList[i])
            }
        }
        return folderList
    }


    fun getImageFolder(): ArrayList<GalleryFolderBean> {
        val projectionPhotos = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val folderList = ArrayList<GalleryFolderBean>()
        val bucketMap = HashMap<Int, GalleryFolderBean>()

        //所有图片
        val allPhotoFolderInfo = GalleryFolderBean(
            0,
            ZyFrameStore.getContext().resources.getString(R.string.all_image),
            null, 0,
            arrayListOf()
        )
        folderList.add(0, allPhotoFolderInfo)

        val cursor = ZyFrameStore.getContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projectionPhotos,
            null,
            null,
            MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )

        cursor?.use {
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getInt(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val imageId = cursor.getLong(imageIdColumn)
                val size = cursor.getLong(sizeColumn)
                val date = cursor.getLong(dateColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId
                )
                if (size > 0) {
                    val photoInfo = GalleryBean(imageId, uri)
                    photoInfo.type = cursor.getString(typeColumn)
                    photoInfo.size = size
                    if (allPhotoFolderInfo.cover == null) {
                        allPhotoFolderInfo.cover = photoInfo
                    }

                    //添加到所有图片
                    allPhotoFolderInfo.list.add(photoInfo)

                    //通过bucketId获取文件夹
                    var photoFolderInfo = bucketMap[bucketId]
                    if (photoFolderInfo == null) {
                        photoFolderInfo = GalleryFolderBean(bucketId, bucketName, photoInfo, date)
                        bucketMap[bucketId] = photoFolderInfo
                        folderList.add(photoFolderInfo)
                    }
                    photoFolderInfo.list.add(photoInfo)
                }
            }
        }
        return folderList
    }


    fun getVideoFolder(): ArrayList<GalleryFolderBean> {
        val projectionPhotos = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_TAKEN
        )

        val folderList = ArrayList<GalleryFolderBean>()
        val bucketMap = HashMap<Int, GalleryFolderBean>()

        //所有视频
        val allVideoFolderInfo = GalleryFolderBean(
            0,
            ZyFrameStore.getContext().resources.getString(R.string.all_video),
            null, 0,
            arrayListOf()
        )
        folderList.add(0, allVideoFolderInfo)

        val cursor = ZyFrameStore.getContext().contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projectionPhotos,
            null,
            null,
            MediaStore.Video.Media.DATE_TAKEN + " DESC"
        )

        cursor?.use {
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val videoIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getInt(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val videoId = cursor.getLong(videoIdColumn)
                val duration = cursor.getInt(durationColumn)

                val size = cursor.getLong(sizeColumn)
                val date = cursor.getLong(dateColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId
                )
                if (size > 0) {
                    val videoInfo = GalleryBean(videoId, uri)
                    videoInfo.duration = duration
                    videoInfo.type = cursor.getString(typeColumn)
                    videoInfo.size = size
                    if (allVideoFolderInfo.cover == null) {
                        allVideoFolderInfo.cover = videoInfo
                    }

                    //添加到所有图片
                    allVideoFolderInfo.list.add(videoInfo)

                    //通过bucketId获取文件夹
                    var photoFolderInfo = bucketMap[bucketId]
                    if (photoFolderInfo == null) {
                        photoFolderInfo = GalleryFolderBean(bucketId, bucketName, videoInfo, date)
                        bucketMap[bucketId] = photoFolderInfo
                        folderList.add(photoFolderInfo)
                    }
                    photoFolderInfo.list.add(videoInfo)
                }
            }
        }
        return folderList
    }
}