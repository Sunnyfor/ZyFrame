package com.sunny.zy.gallery.bean

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Desc 媒体内容
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 18:12
 */
class GalleryBean(var id: Long = 0, var uri: Uri?) : Parcelable {
    var name = ""
    var type = ""
    var duration = 0
    var size = 0L

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Uri::class.java.classLoader)
    ) {
        name = parcel.readString() ?: ""
        type = parcel.readString() ?: ""
        duration = parcel.readInt()
        size = parcel.readLong()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeInt(duration)
        parcel.writeLong(size)
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun toString(): String {
        return "GalleryBean(id=$id, uri=$uri, name='$name', type='$type', duration=$duration, size=$size)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryBean

        if (id != other.id) return false
        if (uri != other.uri) return false
        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (uri?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        return result
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