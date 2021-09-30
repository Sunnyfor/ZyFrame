package com.sunny.zy.gallery.bean

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/22 18:12
 */
data class GalleryContentBean(
    var id: Long = 0,
    var uri: Uri?
) : Parcelable {

    var type = ""
    var duration = 0
    var size = 0

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Uri::class.java.classLoader)
    ) {
        type = parcel.readString() ?: ""
        duration = parcel.readInt()
        size = parcel.readInt()
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(type)
        parcel.writeInt(duration)
        parcel.writeInt(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GalleryContentBean> {
        override fun createFromParcel(parcel: Parcel): GalleryContentBean {
            return GalleryContentBean(parcel)
        }

        override fun newArray(size: Int): Array<GalleryContentBean?> {
            return arrayOfNulls(size)
        }
    }
}