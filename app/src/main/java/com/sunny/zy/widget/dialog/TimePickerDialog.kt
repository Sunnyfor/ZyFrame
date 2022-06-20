package com.sunny.zy.widget.dialog

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sunny.zy.R
import com.sunny.zy.adapter.ArrayWheelAdapter
import com.sunny.zy.base.widget.dialog.BaseDialog
import com.sunny.zy.utils.StringUtil
import com.sunny.zy.widget.wheel.WheelView
import java.util.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2022/3/1 14:36
 */
class TimePickerDialog(context: Context, var resultCallback: (date: String) -> Unit) :
    BaseDialog(context) {

    private val weekStrList = arrayOf("日", "一", "二", "三", "四", "五", "六")

    private val hours = arrayListOf<String>()
    private val minutes = arrayListOf<String>()

    private var hourLabel = "时"
    private var minuteLabel = "分"

    private var dateSb = StringBuilder()
    private var resultSb = StringBuilder()


    private val wvHour by lazy {
        findViewById<WheelView>(R.id.wvHour)
    }

    private val wvMinute by lazy {
        findViewById<WheelView>(R.id.wvMinute)
    }


    override fun initLayout() = R.layout.dialog_time_select

    override fun initView() {
        window?.setGravity(Gravity.BOTTOM)
        findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dismiss()
        }

        findViewById<TextView>(R.id.tvConfirm).setOnClickListener {
            resultSb.clear()
            if (wvHour.currentItem < 10) {
                resultSb.append("0")
            }
            resultSb.append(wvHour.currentItem)
            resultSb.append(":")

            if (wvMinute.currentItem < 10) {
                resultSb.append("0")
            }
            resultSb.append(wvMinute.currentItem)
            resultCallback.invoke(resultSb.toString())
            dismiss()
        }
    }

    override fun loadData() {
        for (i in 0..59) {
            if (i < 10) {
                hours.add("0$i$hourLabel")
                minutes.add("0$i$minuteLabel")
            } else {
                hours.add("$i$hourLabel")
                minutes.add("$i$minuteLabel")
            }
        }
        setWheelViewStyle(wvHour)
        wvHour.setAdapter(ArrayWheelAdapter(hours))

        setWheelViewStyle(wvMinute)
        wvMinute.setAdapter(ArrayWheelAdapter(minutes))

        val currentTime = StringUtil.getCurrentTime("HH:mm")
        val times = currentTime.split(":")
        wvHour.currentItem = hours.indexOf("${times[0]}时")
        wvMinute.currentItem = minutes.indexOf("${times[1]}分")
        initTitle()
    }

    override fun onClickEvent(view: View) {}


    override fun onClose() {}


    private fun initTitle() {
        if (dateSb.isEmpty()) {
            dateSb.append(StringUtil.getCurrentTime("yyyy年MM月dd日"))
            dateSb.append(" 周")
            dateSb.append(weekStrList[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1])
        }
        findViewById<TextView>(R.id.tvTitle)?.text = dateSb.toString()
    }

    fun setTitle(date: String) {
        dateSb.clear()
        dateSb.append(date)
        initTitle()
    }

    private fun setWheelViewStyle(wheelView: WheelView) {
        wheelView.setTextSize(20f)
        wheelView.setTypeface(Typeface.DEFAULT)
        wheelView.setDividerColor(ContextCompat.getColor(context, R.color.color_transparent))
        wheelView.setItemsVisibleCount(5)
        wheelView.setLineSpacingMultiplier(2f)
    }
}