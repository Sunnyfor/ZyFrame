package com.sunny.zy.utils

import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * 封装使用Logger日志代码
 * Created by Zy on 2021年6月30日
 */

object LogUtil {

    const val VERBOSE = 0

    const val DEBUG = 1

    const val INFO = 2

    const val WARN = 3

    const val ERROR = 4


    var onLogListener: OnLogListener? = null


    var tag = "ZYLog"

    var lineSize = 160

    var TOP_BORDER = "┌"
    var BOTTOM_BORDER = "└"
    var MIDDLE_LINE = "├"
    var VERTICAL_LINE = "│ "
    var HORIZONTAL_LINE = "─"


    fun v(message: String) {
        generateBorder(VERBOSE, TOP_BORDER)
        generateLine(VERBOSE, message)
    }

    fun v(title: String, message: String) {
        generateTitle(VERBOSE, title)
        generateBorder(VERBOSE, MIDDLE_LINE)
        generateLine(VERBOSE, message)
    }


    fun d(message: String) {
        generateBorder(DEBUG, TOP_BORDER)
        generateLine(DEBUG, message)
    }

    fun d(title: String, message: String) {
        generateTitle(DEBUG, title)
        generateBorder(DEBUG, MIDDLE_LINE)
        generateLine(DEBUG, message)
    }


    fun i(title: String, message: String) {
        generateTitle(INFO, title)
        generateBorder(INFO, MIDDLE_LINE)
        generateLine(INFO, message)
    }

    fun i(message: String) {
        generateBorder(INFO, TOP_BORDER)
        generateLine(INFO, message)
    }

    fun w(message: String) {
        generateBorder(WARN, TOP_BORDER)
        generateLine(WARN, message)
    }

    fun w(title: String, message: String) {
        generateTitle(WARN, title)
        generateBorder(WARN, MIDDLE_LINE)
        generateLine(WARN, message)
    }

    fun e(message: String) {
        generateBorder(ERROR, TOP_BORDER)
        generateLine(ERROR, message)
    }

    fun e(title: String, message: String) {
        generateTitle(ERROR, title)
        generateBorder(ERROR, MIDDLE_LINE)
        generateLine(ERROR, message)
    }


    private fun generateTitle(logType: Int, title: String) {
        generateBorder(logType, TOP_BORDER)
        val sb = StringBuilder(VERTICAL_LINE)
        sb.append(title)
        println(logType, sb.toString())
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
            } else {
                if (index == msgArray.size - 1) {
                    println(logType, msgSb.toString())
                    generateBorder(logType, BOTTOM_BORDER)
                }
            }
        }
    }


    private fun println(logType: Int, content: String) {

        onLogListener?.onLog(logType, content)

        when (logType) {
            VERBOSE -> Log.v(tag, content)
            DEBUG -> Log.d(tag, content)
            INFO -> Log.i(tag, content)
            WARN -> Log.w(tag, content)
            ERROR -> Log.e(tag, content)
        }
    }


    interface OnLogListener {
        fun onLog(logType: Int, content: String)
    }
}
