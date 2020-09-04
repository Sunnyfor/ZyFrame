package com.sunny.zy.http.bean

import java.io.File

/**
 * Desc 下载结果实体类
 * Author Zy
 * Date 2020/8/24 18:37
 */
abstract class DownLoadResultBean(var fileName: String? = null, var filePath: String? = null) :
    BaseHttpResultBean() {

    var contentLength = 0L //数据长度
    var readLength = 0L  //当前读取长度
    var done = false //是否完成
    var file: File? = null //下载后的文件对象

    abstract fun notifyData(downLoadResultBean: DownLoadResultBean)

    override fun toString(): String {
        return "${super.toString()} DownLoadResultBean(fileName=$fileName, filePath='$filePath', contentLength=$contentLength, readLength=$readLength, done=$done, file=$file)"
    }


}