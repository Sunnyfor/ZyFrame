package com.sunny.zy.http.bean

import java.io.File

/**
 * Desc 下载结果实体类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/8/24 18:37
 */
abstract class DownLoadResultBean(var fileName: String? = null, var filePath: String? = null) :
    BaseHttpResultBean() {

    var contentLength = 0L //数据长度
    var readLength = 0L  //当前读取长度
    var done = false //是否完成
    var file: File? = null //下载后的文件对象
    var progress = 0 //下载进度百分比


    abstract fun notifyData(downLoadResultBean: DownLoadResultBean)

    override fun toString(): String {
        return "${super.toString()} DownLoadResultBean(fileName=$fileName, filePath=$filePath, contentLength=$contentLength, readLength=$readLength, done=$done, file=$file, progress=$progress)"
    }

}

fun getDownLoadResultBean(
    fileName: String? = null,
    filePath: String? = null,
    notifyData: ((bean: DownLoadResultBean) -> Unit)? = null
): DownLoadResultBean {
    return object : DownLoadResultBean(fileName, filePath) {
        override fun notifyData(downLoadResultBean: DownLoadResultBean) {
            notifyData?.invoke(downLoadResultBean)
        }
    }
}