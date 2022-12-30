package com.sunny.zy.callback

import com.sunny.zy.gallery.bean.GalleryBean

interface GallerySelectCallback {
    fun onSelectResult(selectList: ArrayList<GalleryBean>)
}