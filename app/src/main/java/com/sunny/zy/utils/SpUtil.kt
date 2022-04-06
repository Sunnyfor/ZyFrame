package com.sunny.zy.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.sunny.zy.ZyFrameStore
import java.lang.reflect.Type

/**
 * Desc SharedPreferences存储工具类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/6/8 17:17
 */
class SpUtil {

    companion object {

        private val instance = SpUtil()

        fun get(fileName: String? = null): ZySharedPreferences {
            val key = fileName ?: instance.fileName
            var zySharedPreferences = instance.zyShareMap[key]
            if (zySharedPreferences == null) {
                zySharedPreferences = ZySharedPreferences(key)
                instance.zyShareMap[key] = zySharedPreferences
            }
            return zySharedPreferences
        }
    }

    /**
     * 文件名
     */
    private var fileName = "sharedPreferences_info"


    val zyShareMap = HashMap<String, ZySharedPreferences>()

    class ZySharedPreferences(private val fileName: String) {
        private val sharedPreferences: SharedPreferences by lazy {
            ZyFrameStore.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE)
        }

        /**
         * 保存String信息
         */
        fun setString(key: String, content: String) =
            sharedPreferences.edit().putString(key, content).apply()

        fun getString(key: String, content: String): String =
            sharedPreferences.getString(key, null) ?: content

        fun getString(key: String): String = getString(key, "")


        /**
         * 保存Boolean类型的信息
         */
        fun setBoolean(key: String, flag: Boolean) =
            sharedPreferences.edit().putBoolean(key, flag).apply()

        fun getBoolean(key: String, flag: Boolean): Boolean =
            sharedPreferences.getBoolean(key, flag)

        fun getBoolean(key: String): Boolean = getBoolean(key, false)


        /**
         * 保存Integer信息
         */
        fun setInteger(key: String, content: Int = 0) =
            sharedPreferences.edit().putInt(key, content).apply()

        fun getInteger(key: String): Int = getInteger(key, 0)

        fun getInteger(key: String, defValue: Int): Int = sharedPreferences.getInt(key, defValue)

        /**
         * 保存Object信息
         */
        fun setObject(key: String, obj: Any) {
            val gSon = Gson()
            val json = gSon.toJson(obj)
            setString(key, json)
        }

        fun <T> getObject(key: String, clazz: Class<T>): T? {
            val json = getString(key)
            if (TextUtils.isEmpty(json)) {
                return null
            }
            return try {
                val gSon = Gson()
                gSon.fromJson(json, clazz)
            } catch (e: Exception) {
                null
            }

        }


        fun <T> getObject(key: String, type: Type): T? {
            val json = getString(key)
            if (TextUtils.isEmpty(json)) {
                return null
            }
            return try {
                val gSon = Gson()
                gSon.fromJson(json, type)
            } catch (e: Exception) {
                null
            }

        }


        /**
         * 删除元素
         */
        fun remove(key: String) {
            sharedPreferences.edit().remove(key).apply()
        }


        /**
         * 清空share文件
         */
        fun clear() {
            sharedPreferences.edit().clear().apply()
        }
    }

    fun onDestroy() {
        zyShareMap.clear()
    }
}