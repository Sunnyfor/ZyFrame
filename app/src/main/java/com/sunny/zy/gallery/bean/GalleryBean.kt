package com.sunny.zy.gallery.bean

import android.net.Uri

/**
 * Desc 媒体内容
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 18:12
 */
data class GalleryBean(
    var id: Long = 0,
    var uri: Uri?
) {

    var type = ""
    var duration = 0
    var size = 0L
}