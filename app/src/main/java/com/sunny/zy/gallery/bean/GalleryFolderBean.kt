package com.sunny.zy.gallery.bean

/**
 * Desc 媒体文件夹
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 18:03
 */
data class GalleryFolderBean(
    var id: Int,
    var name: String?,
    var cover: GalleryBean?,
    var dateToken: Long,
    var list: ArrayList<GalleryBean> = arrayListOf()
)