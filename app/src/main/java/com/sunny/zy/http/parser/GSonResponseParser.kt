package com.sunny.zy.http.parser

import com.google.gson.Gson
import com.sunny.zy.base.BaseModel
import com.sunny.zy.base.PageModel
import com.sunny.zy.http.ZyConfig
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/4/29 14:51
 */
@Suppress("UNCHECKED_CAST")
class GSonResponseParser : IResponseParser {

    private val mGSon = Gson()

    override fun <T> parserResponse(
        responseBody: ResponseBody,
        type: Type,
        serializedName: String?
    ): T {

        if (type is Class<*>) {
            when (type.name) {
                String::class.java.name -> {
                    return responseBody.string() as T
                }

                File::class.java.name -> {
                    return writeResponseBodyToDisk(responseBody.byteStream(), serializedName) as T
                }
            }
        } else {
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
                            mGSon.fromJson<Any>(jsonObj.optString(serializedName), childType)
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


    private fun writeResponseBodyToDisk(data: InputStream, serializedName: String?): File {
        val pathFile = File(ZyConfig.TEMP)
        if (!pathFile.exists()) {
            pathFile.mkdirs()
        }
        val file = File(pathFile, serializedName ?: "${System.currentTimeMillis()}.temp")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()

        val byte = ByteArray(4096)
        var downloadSize = 0L
        val outputStream = FileOutputStream(file)
        while (true) {
            val read = data.read(byte)
            if (read == -1) {
                break
            }
            outputStream.write(byte, 0, read)
            downloadSize += read
        }
        outputStream.flush()

        return file
    }

}