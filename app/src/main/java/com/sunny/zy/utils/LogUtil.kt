package com.sunny.zy.utils

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * 封装使用Logger日志代码
 * Created by Zy on 2021年6月30日
 */

object LogUtil {

    private const val divider = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"

    var isLoggable = true
    var tag = "ZyFrame"

    init {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .tag(tag)
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return isLoggable
            }
        })
    }


    fun log(priority: Int, tag: String, message: String?, throwable: Throwable?) {
        Logger.log(priority, tag, message, throwable)
    }

    fun d(message: String, vararg args: Any?) {
        Logger.d(message, *args)
    }

    fun d(title: String, message: String, vararg args: Any?) {
        d(generateTitle(title).append(message).toString(), *args)
    }

    fun e(message: String, vararg args: Any?) {
        Logger.e(null, message, *args)
    }

    fun e(title: String, message: String, vararg args: Any?) {
        e(generateTitle(title).append(message).toString(), *args)
    }


    fun i(message: String, vararg args: Any?) {
        Logger.i(message, *args)
    }

    fun i(title: String, message: String, vararg args: Any?) {
        i(generateTitle(title).append(message).toString(), *args)
    }

    fun v(message: String, vararg args: Any?) {
        Logger.v(message, *args)
    }

    fun v(title: String, message: String, vararg args: Any?) {
        v(generateTitle(title).append(message).toString(), *args)
    }

    fun w(message: String, vararg args: Any?) {
        Logger.w(message, *args)
    }

    fun w(title: String, message: String, vararg args: Any?) {
        w(generateTitle(title).append(message).toString(), *args)
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    fun wtf(message: String, vararg args: Any?) {
        Logger.wtf(message, *args)
    }

    fun wtf(title: String, message: String, vararg args: Any?) {
        wtf(generateTitle(title).append(message).toString(), *args)
    }

    /**
     * Formats the given json content and print it
     */
    fun json(json: String) {
        Logger.json(json)
    }

    /**
     * Formats the given xml content and print it
     */
    fun xml(xml: String) {
        Logger.xml(xml)
    }

    private fun generateTitle(title: String): StringBuilder {
        return StringBuilder(title).append("\n").append(divider).append("\n")
    }

}
