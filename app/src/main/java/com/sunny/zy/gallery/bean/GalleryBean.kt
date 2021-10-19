package com.sunny.zy.gallery.bean

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Desc 媒体内容
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/22 18:12
 */
data class GalleryBean(
    var id: Long = 0,
    var uri: Uri?
) : Parcelable {

    var type = ""
    var duration = 0
    var size = 0L

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Uri::class.java.classLoader)
    ) {
        type = parcel.readString() ?: ""
        duration = parcel.readInt()
        size = parcel.readLong()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is GalleryBean) {
            return false
        }
        return other.uri == uri
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + uri.hashCode()
        return result.toInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(type)
        parcel.writeInt(duration)
        parcel.writeLong(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GalleryBean> {
        override fun createFromParcel(parcel: Parcel): GalleryBean {
            return GalleryBean(parcel)
        }

        override fun newArray(size: Int): Array<GalleryBean?> {
            return arrayOfNulls(size)
        }
    }
}