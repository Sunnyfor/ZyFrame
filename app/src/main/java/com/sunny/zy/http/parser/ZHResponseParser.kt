package com.sunny.zy.http.parser

import com.google.gson.Gson
import com.sunny.zy.base.BaseModel
import com.sunny.zy.base.PageModel
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.reflect.ParameterizedType

/**
 * Desc 仅针对【贞和科技】数据解析器
 * Author Zy
 * Date 2020/4/29 14:51
 */
@Suppress("UNCHECKED_CAST")
class ZHResponseParser : IResponseParser {

    val mGSon = Gson()

    override fun <T> parserHttpResponse(
        responseBody: ResponseBody,
        httpResultBean: HttpResultBean<T>
    ): T {

        val type = httpResultBean.typeToken

        //解析非泛型类
        if (type is Class<*>) {
            return when (type.name) {
                String::class.java.name -> {
                    responseBody.string() as T
                }
                else -> {
                    mGSon.fromJson(responseBody.string(), type) as T
                }
            }
        } else {
            //解析泛型类
            val json = responseBody.string()
            if (type is ParameterizedType) {
                val jsonObj = JSONObject(json)
                when (type.rawType) {

                    BaseModel::class.java -> {
                        val childType = type.actualTypeArguments[0]
                        val baseModel = BaseModel<Any>()
                        baseModel.msg = jsonObj.optString("msg")
                        baseModel.code = jsonObj.optString("code")
                        val mData =
                            mGSon.fromJson<Any>(
                                jsonObj.optString(httpResultBean.serializedName),
                                childType
                            )
                        baseModel.data = mData
                        return baseModel as T
                    }
                    PageModel::class.java -> {
                        jsonObj.put("data", jsonObj.optJSONObject("page"))
                        jsonObj.remove("page")
                        return mGSon.fromJson(jsonObj.toString(), type)
                    }
                }
            }
        }
        return mGSon.fromJson(responseBody.string(), type)
    }

    override fun parserDownloadResponse(
        responseBody: ResponseBody,
        downLoadResultBean: DownLoadResultBean
    ): File {
        return writeResponseBodyToDisk(responseBody.byteStream(), downLoadResultBean)
    }


    /**
     * 写入SDK
     */
    private fun writeResponseBodyToDisk(
        data: InputStream,
        downLoadResultBean: DownLoadResultBean
    ): File {
        if (downLoadResultBean.filePath == null) {
            downLoadResultBean.filePath = ZyConfig.TEMP
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

        downLoadResultBean.scope.launch {
            withContext(IO) {
                while (true) {
                    val read = data.read(byte)
                    if (read == -1) {
                        break
                    }
                    totalRead += read
                    outputStream.write(byte, 0, read)

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