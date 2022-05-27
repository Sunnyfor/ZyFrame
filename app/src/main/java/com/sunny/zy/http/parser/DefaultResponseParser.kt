package com.sunny.zy.http.parser

import com.google.gson.Gson
import com.sunny.zy.ZyFrameConfig
import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Desc 默认解析器
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/4/29 14:51
 */
@Suppress("UNCHECKED_CAST")
open class DefaultResponseParser : IResponseParser {

    open val mGSon = Gson()

    override fun <T> parserHttpResponse(
        responseBody: ResponseBody,
        httpResultBean: HttpResultBean<T>
    ): T {

        val type = httpResultBean.type
        val body = responseBody.string()

        if (type.toString() == String::class.java.toString()) {
            return body as T
        }

        //解析泛型类
        return mGSon.fromJson(body, type)
    }

    override fun parserDownloadResponse(
        responseBody: ResponseBody,
        downLoadResultBean: DownLoadResultBean
    ): File {
        return writeResponseBodyToDisk(responseBody.byteStream(), downLoadResultBean)
    }


    /**
     * 文件写入SD卡
     */
    private fun writeResponseBodyToDisk(
        data: InputStream,
        downLoadResultBean: DownLoadResultBean
    ): File {

        if (downLoadResultBean.filePath == null) {
            downLoadResultBean.filePath = ZyFrameConfig.TEMP
        }

        val pathFile = File(downLoadResultBean.filePath ?: "")
        if (!pathFile.exists()) {
            pathFile.mkdirs()
        }

        if (downLoadResultBean.fileName == null || downLoadResultBean.fileName?.isEmpty() == true) {
            downLoadResultBean.fileName = "${System.currentTimeMillis()}.temp"
        }

        val file = File(pathFile, downLoadResultBean.fileName ?: "")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()

        val byte = ByteArray(4096)
        val outputStream = FileOutputStream(file)

        var totalRead = 0L

        downLoadResultBean.scope?.launch(IO) {
            while (true) {
                val read = data.read(byte)
                if (read == -1) {
                    break
                }
                totalRead += read
                outputStream.write(byte, 0, read)

                if (downLoadResultBean.contentLength != 0L) {
                    val progress =
                        50 + (totalRead * 100 / downLoadResultBean.contentLength).toInt() / 2
                    if (progress != downLoadResultBean.progress) {
                        withContext(Main) {
                            downLoadResultBean.progress = progress
                            downLoadResultBean.done = totalRead == downLoadResultBean.contentLength
                            downLoadResultBean.notifyData(downLoadResultBean)
                        }
                    }
                }
            }
        }
        outputStream.flush()
        return file
    }

}