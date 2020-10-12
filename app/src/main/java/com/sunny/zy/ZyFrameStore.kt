package com.sunny.zy

import android.content.Context
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.LogUtil
import java.util.*

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
    fun finishActivity(activity: BaseActivity) {
        val list = arrayListOf<BaseActivity>()
        activityStack.forEach {
            if (it.taskTag == activity.taskTag) {
                it.finish()
                list.add(it)
            }
        }
        activityStack.removeAll(list)
    }

    /**
     * 保留指定TaskTag的Activity
     */
    fun keepActivity(activity: BaseActivity) {
        val list = arrayListOf<BaseActivity>()
        activityStack.forEach {
            if (it.taskTag != activity.taskTag) {
                list.add(it)
                it.finish()
            }
        }
        activityStack.removeAll(list)
    }

    /**
     * 关闭指定页面
     */
    fun finishActivity(activityName: String) {
        activityStack.find { it.javaClass.simpleName == activityName }?.let {
            it.finish()
            activityStack.remove(it)

        }
    }

    /**
     * 关闭所有的Activity
     */
    fun finishAllActivity(activity: BaseActivity? = null) {
        activityStack.forEach {
            if (activity != it) {
                LogUtil.i("关闭:${activity?.packageName}")
                it.finish()
            }
        }
        activityStack.clear()
        activity?.let {
            activityStack.add(activity)
        }
    }

    /**
     * 保留指定Activity的类
     */
    fun keepSomeActivity(activityName: String) {
        var keepActivity: BaseActivity? = null
        activityStack.forEach {
            if (activityName == it.javaClass.simpleName) {
                LogUtil.i("保留:${it?.packageName}")
                keepActivity = it
            } else {
                it.finish()
            }
        }
        activityStack.clear()
        activityStack.add(keepActivity)
    }

    /**
     * 保留指定Activity的类
     */
    fun keepSomeActivity(list: List<String>) {
        val keepActivity = ArrayList<BaseActivity>()
        activityStack.forEach {
            if (list.contains(it.javaClass.simpleName)) {
                LogUtil.i("保留:${it?.packageName}")
                keepActivity.add(it)
            } else {
                it.finish()
            }
        }
        activityStack.clear()
        activityStack.addAll(keepActivity)
    }

}