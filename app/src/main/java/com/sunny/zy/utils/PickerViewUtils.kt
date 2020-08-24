package com.sunny.zy.utils

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.sunny.zy.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description:
 * @Author:         张野
 * @CreateDate:     2018/10/18 14:35
 */
class PickerViewUtils(var context: Context) {

    //显示年月日选择器
    fun showYmd(onYmdResult: (dateStr: String, timestamp: String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            R.style.Theme_MaterialComponents_Dialog_Alert,
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                onYmdResult(
                    dateFormat.format(calendar.time),
                    (calendar.time.time / 1000).toString()
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        val llFirst = datePickerDialog.datePicker.getChildAt(0)
        val mSpinners = (llFirst as LinearLayout).getChildAt(0) as LinearLayout
        for (i in 0 until mSpinners.childCount) {
            val picker = mSpinners.getChildAt(i)
            val pickerFields = NumberPicker::class.java.declaredFields
            pickerFields.forEach {
                if (it.name == "mSelectionDivider") {
                    it.isAccessible = true
                    it.set(picker, ColorDrawable(ContextCompat.getColor(context, R.color.red)))
                }
            }
        }
        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.show()
    }
}