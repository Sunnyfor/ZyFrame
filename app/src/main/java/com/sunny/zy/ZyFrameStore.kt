package com.sunny.zy

import android.app.Application
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.LogUtil
import java.util.*
import kotlin.system.exitProcess

/**
 * Desc 应用类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/08/02.
 */
object ZyFrameStore {

    private lateinit var instance: Application

    private val activityStack = Stack<BaseActivity>()

    var activityCycle: IActivityCycle? = null

    fun init(application: Application) {
        instance = application
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

    /**
     * 清空所有内存数据
     */
    fun removeAllData() {
        storeMap.clear()
    }

    /**
     * 存储管理Activity
     */
    fun addActivity(baseActivity: BaseActivity) {
        activityStack.add(baseActivity)
        activityCycle?.onAdd(baseActivity)
    }

    /**
     * 移除Activity
     */
    fun removeActivity(baseActivity: BaseActivity) {
        activityStack.remove(baseActivity)
        activityCycle?.onRemove(baseActivity)
    }

    /**
     * 根据下标获取Activity
     */
    fun getActivity(index: Int): BaseActivity {
        return activityStack[index]
    }

    /**
     * 获取Activity的实例数量
     */
    fun getActivitySize(clazz: Class<*> = BaseActivity::class.java): Int {
        if (clazz != BaseActivity::class.java) {
           return activityStack.filter { it.javaClass == clazz }.size
        }
        return activityStack.size
    }

    fun getLastBaseActivity(): BaseActivity {
        return getActivity(getActivitySize() - 1)
    }

    fun getFastBaseActivity(): BaseActivity {
        return getActivity(0)
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
    }

    /**
     * 获取指定Class的Activity实例
     * @return 如果存在的话返回具体实例用于调用内部方法
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : BaseActivity> getActivity(clazz: Class<T>, position: Int? = null): T? {
        val result = activityStack.filter { it.javaClass == clazz }
        if (result.isNotEmpty()) {
            val index = position ?: result.size - 1
            if (index < result.size) {
                return result[index] as T
            }
        }
        return null
    }

    interface IActivityCycle {
        fun onAdd(activity: BaseActivity)
        fun onRemove(activity: BaseActivity)
    }
}