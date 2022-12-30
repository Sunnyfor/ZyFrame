package com.sunny.zy.callback

import com.sunny.zy.gallery.bean.GalleryBean

interface GalleryPreviewCallback {
    fun onResult(flag: Boolean, resultList: ArrayList<GalleryBean>)
}