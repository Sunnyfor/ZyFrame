package com.sunny.zy

import android.content.Context
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.LogUtil
import java.util.*
import kotlin.system.exitProcess

/**
 * 应用类
 * Created by Zy on 2018/08/02.
 */
object ZyFrameStore {

    private lateinit var instance: Context

    private val activityStack = Stack<BaseActivity>()


    fun init(context: Context) {
        instance = context.applicationContext
    }

    fun getContext() = instance

    private val storeMap = HashMap<String, Any>() //内存数据存储

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(key: String, isDelete: Boolean = false): T? {

        if (!storeMap.containsKey(key)) {
            return null
        }

        val result = storeMap[key]

        if (isDelete) {
            removeData(key)
        }
        return result as T
    }


    /**
     * 存储数据
     */
    fun setData(key: String, t: Any?) {
        if (t != null) {
            storeMap[key] = t
        }
    }

    /**
     * 删除数据
     */
    fun removeData(key: String) {
        storeMap.remove(key)
    }

    fun removeAllData() {
        storeMap.clear()
    }

    /**
     * 存储管理Activity
     */
    fun addActivity(baseActivity: BaseActivity) {
        activityStack.add(baseActivity)
    }

    /**
     * 移除Activity
     */
    fun removeActivity(baseActivity: BaseActivity) {
        activityStack.remove(baseActivity)
    }


    /**
     * 关闭指定TaskTag的Activity
     */
    fun finishTagActivity(tagActivity: BaseActivity) {
        val list = arrayListOf<BaseActivity>()
        activityStack.forEach {
            if (it.taskTag == tagActivity.taskTag) {
                it.finish()
                list.add(it)
            }
        }
        activityStack.removeAll(list)
    }

    /**
     * 保留指定TaskTag的Activity
     */
    fun keepTagActivity(tagActivity: BaseActivity) {
        val list = arrayListOf<BaseActivity>()
        activityStack.forEach {
            if (it.taskTag != tagActivity.taskTag) {
                list.add(it)
                it.finish()
            }
        }
        activityStack.removeAll(list)
    }


    /**
     * 关闭所有的Activity
     */
    fun finishAllActivity() {
        activityStack.forEach {
            LogUtil.i("关闭:${it?.javaClass?.name}")
            it.finish()
        }
        activityStack.clear()
        exitProcess(0)
    }

    /**
     * 获取指定类型的Activity实例
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : BaseActivity> getActivity(clazz: Class<T>): T {
        return activityStack.find { it.javaClass == clazz } as T
    }
}