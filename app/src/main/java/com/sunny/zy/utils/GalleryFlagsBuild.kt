package com.sunny.zy.utils

import android.os.Bundle
import com.sunny.zy.gallery.GallerySelectActivity

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/18 16:16
 */
class GalleryFlagsBuild {

    private val bundle = Bundle()

    fun setSelectType(type: Int): GalleryFlagsBuild {
        bundle.putInt(GallerySelectActivity.SELECT_TYPE_INT, type)
        return this
    }

    fun setMaxSize(size: Int): GalleryFlagsBuild {
        bundle.putInt(GallerySelectActivity.MAX_SIZE_INT, size)
        return this
    }

    fun setFileType(fileType: Int): GalleryFlagsBuild {
        bundle.putInt(GallerySelectActivity.FILE_TYPE_INT, fileType)
        return this
    }

    fun isCrop(isCrop: Boolean): GalleryFlagsBuild {
        bundle.putBoolean(GallerySelectActivity.IS_CROP_BOOLEAN, isCrop)
        return this
    }

    fun setAspectX(aspectX: Int): GalleryFlagsBuild {
        bundle.putInt(GallerySelectActivity.ASPECT_X_INT, aspectX)
        return this
    }

    fun setAspectY(aspectY: Int): GalleryFlagsBuild {
        bundle.putInt(GallerySelectActivity.ASPECT_X_INT, aspectY)
        return this
    }


    fun build(): Bundle {
        return bundle
    }


}