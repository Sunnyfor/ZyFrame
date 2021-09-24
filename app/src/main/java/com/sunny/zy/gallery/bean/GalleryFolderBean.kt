package com.sunny.zy.gallery.bean

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/22 18:03
 */
data class GalleryFolderBean(
    var id: Int,
    var name: String,
    var cover: GalleryContentBean?,
    var dateToken:Long,
    var list: ArrayList<GalleryContentBean> = arrayListOf()
)