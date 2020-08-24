package com.sunny.zy.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Desc 调取系统日期
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
 * Date 2019/11/1 15:33
 */
object TimerPackUtil {

    private val timeSb = StringBuilder()

    private val calendar = Calendar.getInstance()

    private fun showDatePack(context: Context, onTimePackResult: OnTimePackResult, isResult: Boolean) {
        timeSb.clear()
        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            timeSb.append(year).append("-")
            if (month + 1 < 10) {
                timeSb.append("0")
            }
            timeSb.append(month + 1).append("-")

            if (dayOfMonth < 10) {
                timeSb.append("0")
            }
            timeSb.append(dayOfMonth)
            if (!isResult) {
                timeSb.append(" ")
                showTimePack(context, onTimePackResult, false)
            } else {
                onTimePackResult.onSelect(timeSb.toString())
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun showDatePack(context: Context, onTimePackResult: OnTimePackResult) {
        showDatePack(context, onTimePackResult, true)
    }


    private fun showTimePack(context: Context, onTimePackResult: OnTimePackResult, isClear: Boolean) {
        if (isClear) {
            timeSb.clear()
        }
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            timeSb.append(if (hourOfDay < 10) "0$hourOfDay" else hourOfDay).append(":")
                    .append(if (minute < 10) "0$minute" else minute).append(":").append("00")
            onTimePackResult.onSelect(timeSb.toString())
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    fun showTimePack(context: Context, onTimePackResult: OnTimePackResult) {
        showTimePack(context, onTimePackResult, true)
    }


    fun showDateAndTimePack(context: Context, onTimePackResult: OnTimePackResult) {
        showDatePack(context, onTimePackResult, false)
    }


    /**
     * 选择时间并给相应的组件设置时间
     */
    fun showDateAndTimePack(context: Context, view: TextView) {
        showDateAndTimePack(context, object : OnTimePackResult {
            override fun onSelect(timeStr: String) {
                view.text = timeStr
            }
        })
    }

    fun showCurrentTime(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = simpleDateFormat.format(Date())
        LogUtil.i("JoannChen -- 当前时间：$currentTime")
        return currentTime
    }

    interface OnTimePackResult {
        fun onSelect(timeStr: String)
    }
}