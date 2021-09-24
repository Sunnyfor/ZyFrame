package com.sunny.zy.gallery.bean

import android.net.Uri

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/22 18:12
 */
data class GalleryContentBean(
    var id: Long = 0,
    var uri: Uri
) {
    //0为图片 1为视频
    var type = 0
    var duration = 0
    var size = 0

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is GalleryContentBean) {
            return false
        }
        return other.uri == uri
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + uri.hashCode()
        return result.toInt()
    }
}