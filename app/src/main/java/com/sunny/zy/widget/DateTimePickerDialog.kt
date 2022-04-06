package com.sunny.zy.widget

import android.content.Context

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2022/3/1 16:50
 */
class DateTimePickerDialog(var context: Context, var resultCallback: (result: String) -> Unit) {
    private val timeSb = StringBuilder()

    private val datePickerDialog: DatePickerDialog by lazy {
        DatePickerDialog(context) {
            timeSb.clear()
            timeSb.append(it)
            timePickerDialog.setTitle(datePickerDialog.getTitle())
            timePickerDialog.show()
        }
    }

    private val timePickerDialog: TimePickerDialog by lazy {
        TimePickerDialog(context) {
            timeSb.append(" ")
            timeSb.append(it)
            resultCallback.invoke(timeSb.toString())
        }
    }


    fun setYear(year: Int) {
        datePickerDialog.setYear(year)
    }

    fun setMonth(month: Int) {
        datePickerDialog.setMonth(month)
    }


    fun setDay(day: Int) {
        datePickerDialog.setDay(day)
    }


    fun setDate(date: String) {
        datePickerDialog.setDate(date)
    }


    fun show() {
        datePickerDialog.show()
    }
}