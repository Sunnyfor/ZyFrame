package com.sunny.zy.utils

import android.content.Context
import com.bigkoo.pickerview.builder.TimePickerBuilder

/**
 * Desc 调取系统日期
 * Author Zy
 * Date 2019/11/1 15:33
 */
object TimerPickUtil {

    private val timeSb = StringBuilder()

    private fun showDate(
        context: Context,
        onTimePackResult: OnTimePackResult,
        isResult: Boolean,
        isSecond: Boolean,
        format: String
    ) {
        timeSb.clear()
        TimePickerBuilder(context) { date, _ ->
            timeSb.append(StringUtil.formatTime(format, date))
            if (!isResult) {
                timeSb.append(" ")
                if (isSecond) {
                    showTimeSecond(context, onTimePackResult, false)
                } else {
                    showTime(context, onTimePackResult, false)
                }
            } else {
                onTimePackResult.onSelect(timeSb.toString())
            }
        }
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .build().show()
    }


    /**
     * 年月选择器
     */
    fun showYearMonth(
        context: Context,
        onTimePackResult: OnTimePackResult,
        format: String = "yyyy-MM"
    ) {
        timeSb.clear()
        TimePickerBuilder(context) { date, _ ->
            timeSb.append(StringUtil.formatTime(format, date))
            onTimePackResult.onSelect(timeSb.toString())
        }
            .setType(booleanArrayOf(true, true, false, false, false, false))
            .build().show()
    }


    /**
     * 年月日选择器
     */
    fun showDate(
        context: Context,
        onTimePackResult: OnTimePackResult,
        format: String = "yyyy-MM-dd"
    ) {
        showDate(context, onTimePackResult, isResult = true, isSecond = false, format = format)
    }

    /**
     * 时分选择器
     */
    private fun showTime(
        context: Context,
        onTimePackResult: OnTimePackResult,
        isClear: Boolean
    ) {
        if (isClear) {
            timeSb.clear()
        }
        TimePickerBuilder(context) { date, _ ->
            timeSb.append(StringUtil.formatTime("HH:mm", date))
            onTimePackResult.onSelect(timeSb.toString())
        }
            .setType(booleanArrayOf(false, false, false, true, true, false))
            .build().show()
    }

    /**
     * 时分选择器
     */
    fun showTime(context: Context, onTimePackResult: OnTimePackResult) {
        showTime(context, onTimePackResult, true)
    }


    /**
     * 时分秒选择器
     */
    private fun showTimeSecond(
        context: Context,
        onTimePackResult: OnTimePackResult,
        isClear: Boolean
    ) {
        if (isClear) {
            timeSb.clear()
        }
        TimePickerBuilder(context) { date, _ ->
            timeSb.append(StringUtil.formatTime("HH:mm:ss", date))
            onTimePackResult.onSelect(timeSb.toString())
        }
            .setType(booleanArrayOf(false, false, false, true, true, true))
            .build().show()
    }

    /**
     * 时分秒选择器
     */
    fun showTimeSecond(
        context: Context,
        onTimePackResult: OnTimePackResult
    ) {
        showTimeSecond(context, onTimePackResult, true)
    }


    /**
     * 年月日时分选择器
     */
    fun showDateAndTime(
        context: Context,
        onTimePackResult: OnTimePackResult,
        format: String = "yyyy-MM-dd"
    ) {
        showDate(context, onTimePackResult, false, isSecond = false, format = format)
    }


    /**
     * 年月日时分选择器
     */
    fun showDateAndTimeSecond(
        context: Context,
        onTimePackResult: OnTimePackResult,
        format: String = "yyyy-MM-dd"
    ) {
        showDate(context, onTimePackResult, false, isSecond = true, format = format)
    }


    interface OnTimePackResult {
        fun onSelect(timeStr: String)
    }
}