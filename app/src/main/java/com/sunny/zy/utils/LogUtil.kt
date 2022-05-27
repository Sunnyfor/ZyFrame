package com.sunny.zy.utils

import android.util.Log
import com.sunny.zy.ZyFrameConfig

/**
 * Desc 封装使用Log日志代码
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021年6月30日
 */
object LogUtil {

    const val VERBOSE = 0

    const val DEBUG = 1

    const val INFO = 2

    const val WARN = 3

    const val ERROR = 4

    var onLogListener: OnLogListener? = null

    var steIndex = 5

    var lineSize = 160

    var TOP_BORDER = "┌"
    var BOTTOM_BORDER = "└"
    var MIDDLE_LINE = "├"
    var VERTICAL_LINE = "│ "
    var HORIZONTAL_LINE = "─"

    var TITLE_START = "【"

    var TITLE_END = "】"

    /**
     * VERBOSE
     */
    @Synchronized
    fun v(message: String) {
        log(VERBOSE, "", message, true)
    }

    @Synchronized
    fun v(title: String, message: String) {
        log(VERBOSE, title, message, true)
    }

    @Synchronized
    fun v(title: String, message: String, isShowSource: Boolean) {
        log(VERBOSE, title, message, isShowSource)
    }


    /**
     *  DEBUG
     */
    @Synchronized
    fun d(message: String) {
        log(DEBUG, "", message, true)
    }

    @Synchronized
    fun d(title: String, message: String) {
        log(DEBUG, title, message, true)
    }

    @Synchronized
    fun d(title: String, message: String, isShowSource: Boolean) {
        log(DEBUG, title, message, isShowSource)
    }

    /**
     *  INFO
     */
    @Synchronized
    fun i(message: String) {
        log(INFO, "", message, true)
    }

    @Synchronized
    fun i(title: String, message: String) {
        log(INFO, title, message, true)
    }

    @Synchronized
    fun i(title: String, message: String, isShowSource: Boolean) {
        log(INFO, title, message, isShowSource)
    }


    /**
     * WARN
     */
    @Synchronized
    fun w(message: String) {
        log(WARN, "", message, true)
    }

    @Synchronized
    fun w(title: String, message: String) {
        log(WARN, title, message, true)
    }

    @Synchronized
    fun w(title: String, message: String, isShowSource: Boolean) {
        log(WARN, title, message, isShowSource)
    }

    /**
     *  ERROR
     */
    @Synchronized
    fun e(message: String) {
        log(ERROR, "", message, true)
    }

    @Synchronized
    fun e(title: String, message: String) {
        log(ERROR, title, message, true)
    }

    @Synchronized
    fun e(title: String, message: String, isShowSource: Boolean) {
        log(ERROR, title, message, isShowSource)
    }


    private fun generateTitle(logType: Int, title: String, isShowSource: Boolean) {
        val sb = StringBuilder(VERTICAL_LINE)
        sb.append("$TITLE_START$title$TITLE_END")
        if (isShowSource) {
            sb.append("  ")
            sb.append(getSourceStr(1))
        }
        println(logType, sb.toString())

    }


    private fun getSourceStr(complement: Int): String {
        val ste = Thread.currentThread().stackTrace
        if (ste.size >= steIndex + complement) {
            return ste[steIndex + complement].toString()
        }
        return ""
    }


    private fun generateBorder(logType: Int, borderType: String) {
        val sb = StringBuilder()
        sb.append(borderType)
        var isEnd = true
        while (isEnd) {
            sb.append(HORIZONTAL_LINE)
            val size = sb.toString().toByteArray().size
            if (size >= lineSize * 3 || size >= 4000) {
                isEnd = false
            }
        }
        println(logType, sb.toString())
    }


    private fun generateLine(logType: Int, content: String) {
        val msgArray = content.toCharArray()
        val msgSb = StringBuilder()
        msgSb.append(VERTICAL_LINE)
        msgArray.forEachIndexed { index, c ->
            if (c != '\n') {
                msgSb.append(c)
            }
            val size = msgSb.toString().toByteArray().size
            if (size >= lineSize || c == '\n') {
                println(logType, msgSb.toString())
                msgSb.clear()
                msgSb.append(VERTICAL_LINE)
            }

            if (index == msgArray.size - 1) {
                if (msgSb.isNotEmpty()) {
                    println(logType, msgSb.toString())
                }
                generateBorder(logType, BOTTOM_BORDER)
            }

        }
    }


    private fun log(logType: Int, title: String, message: String, isShowSource: Boolean) {
        generateBorder(logType, TOP_BORDER)
        if (title.isNotEmpty()) {
            generateTitle(logType, title, isShowSource)
        } else {
            if (isShowSource) {
                println(logType, VERTICAL_LINE + getSourceStr(0))
            }
        }
        generateBorder(logType, MIDDLE_LINE)
        generateLine(logType, message)
    }


    private fun println(logType: Int, content: String) {

        if (!ZyFrameConfig.isPrintLog) {
            return
        }

        if (onLogListener != null) {
            onLogListener?.onLog(logType, content)
            return
        }

        when (logType) {
            VERBOSE -> Log.v(ZyFrameConfig.logTag, content)
            DEBUG -> Log.d(ZyFrameConfig.logTag, content)
            INFO -> Log.i(ZyFrameConfig.logTag, content)
            WARN -> Log.w(ZyFrameConfig.logTag, content)
            ERROR -> Log.e(ZyFrameConfig.logTag, content)
        }
    }


    interface OnLogListener {
        fun onLog(logType: Int, content: String)
    }
}
