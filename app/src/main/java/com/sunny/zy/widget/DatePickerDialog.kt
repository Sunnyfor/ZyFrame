package com.sunny.zy.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import com.sunny.zy.R
import com.sunny.zy.adapter.ArrayWheelAdapter
import com.sunny.zy.base.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_date_select.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Desc 定制化日期选择组件
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2022/2/25 17:36
 */
class DatePickerDialog(context: Context, var resultCallback: (date: String) -> Unit) :
    BaseDialog(context, R.layout.dialog_date_select) {

    private val calendar by lazy {
        Calendar.getInstance()
    }

    private val years = arrayListOf<String>()

    private val months = arrayListOf<String>()

    private val days = arrayListOf<String>()

    private val dateSb = StringBuilder()
    private val weekSb = StringBuilder()

    private val weekStrList = arrayOf("日", "一", "二", "三", "四", "五", "六")

    private var labelYear = "年"
    private var labelMonth = "月"
    private var labelDay = "日"

    private var montIndex = 1

    private var dayIndex = 1

    private var isShowYear = true

    private var isShowMonth = true

    private var isShowDay = true

    private var title = ""

    var isNoLimit = false //月和日是否不限制

    var dateSplit = "-" //日期分隔符

    fun setYear(year: Int) {
        calendar.set(Calendar.YEAR, year)
    }

    fun setMonth(month: Int) {
        calendar.set(Calendar.MONTH, month - 1)
    }


    fun setDay(day: Int) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
    }


    fun setLabel(year: String, month: String, day: String) {
        labelYear = year
        labelMonth = month
        labelDay = day
    }


    fun setDate(date: String) {
        val dates = date.split(dateSplit)
        if (dates.isNotEmpty()) {
            setYear(dates[0].toInt())
        }
        if (dates.size >= 2) {
            setMonth(dates[1].toInt())
        } else {
            setMonth(1)
        }
        if (dates.size >= 3) {
            setDay(dates[2].toInt())
        } else {
            setDay(1)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWheelViewStyle(wvYear)
        wvYear.setOnItemSelectedListener {
            val pattern = Pattern.compile("^[0-9]*")
            val matcher = pattern.matcher(years[it])
            matcher.find()
            calendar.set(Calendar.YEAR, matcher.group().toInt())
            initDay()
            initTitle()
        }

        setWheelViewStyle(wvMonth)
        wvMonth.setOnItemSelectedListener {
            montIndex = it
            if (isNoLimit && it == 0) {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                initDay()
                initTitle()
                return@setOnItemSelectedListener
            }

            val position = if (isNoLimit) 1 else 0

            val mCalendar = Calendar.getInstance()
            mCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            mCalendar.set(Calendar.DAY_OF_MONTH, 1)
            mCalendar.set(Calendar.MONTH, it - position)

            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val maxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (currentDay >= maxDay) {
                calendar.set(Calendar.DAY_OF_MONTH, maxDay)
            }

            calendar.set(Calendar.MONTH, it - position)
            initDay()
            initTitle()
        }

        setWheelViewStyle(wvDay)
        wvDay.setOnItemSelectedListener {
            dayIndex = it
            if (isNoLimit && it == 0) {
                initTitle()
                return@setOnItemSelectedListener
            }
            val position = if (isNoLimit) 1 else 0
            calendar.set(Calendar.DAY_OF_MONTH, it + 1 - position)
            initTitle()
        }

        tvCancel.setOnClickListener {
            dismiss()
        }

        tvConfirm.setOnClickListener {
            val regex = Regex("[^0-9]")
            val resultSb = StringBuilder(dateSb.replace(regex, dateSplit))

            if (resultSb.isNotEmpty() && resultSb.lastIndexOf(dateSplit) == resultSb.length - 1) {
                resultSb.deleteCharAt(resultSb.length - 1)
            }
            resultCallback.invoke(
                resultSb.toString()
            )
            dismiss()
        }

        initYear()
        initMonth()
        initDay()
        initTitle()
        if (isNoLimit) {
            wvMonth.setCyclic(false)
            wvDay.setCyclic(false)
        }
    }


    private fun initYear() {
        /**
         * 初始化年数据
         */
        if (years.isNotEmpty()) {
            return
        }
        val currentYear = calendar.get(Calendar.YEAR)
        years.clear()
        val currentYearStr = "${currentYear}$labelYear"
        years.add(currentYearStr)
        for (i in 1..100) {
            years.add("${currentYear + i}$labelYear")
            years.add(0, "${currentYear - i}$labelYear")
        }
        wvYear.adapter = ArrayWheelAdapter(years)
        wvYear.currentItem = years.indexOf(currentYearStr)
    }


    private fun initMonth(only: Boolean = false) {
        /**
         *  初始化月数据
         */
        var size = 0
        if (months.isEmpty()) {

            if (isNoLimit) {
                months.add("不限")
                size = 1
            }

            for (i in 1..12) {
                months.add("$i$labelMonth")
            }
            wvMonth.adapter = ArrayWheelAdapter(months)
        }

        if (only) {
            wvMonth.currentItem = 0
        } else {
            wvMonth.currentItem = calendar.get(Calendar.MONTH) + size
        }
    }


    private fun initDay() {
        /**
         * 初始化天数据
         */
        var size = 0
        days.clear()
        if (isNoLimit) {
            days.add("不限")
            size = 1
        }

        if (isNoLimit && montIndex == 0) {
            wvDay.adapter = ArrayWheelAdapter(days)
            dayIndex = 0
            wvDay.currentItem = dayIndex
        } else {
            for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                days.add("$i$labelDay")
            }
            wvDay.adapter = ArrayWheelAdapter(days)
            if (dayIndex != 0) {
                wvDay.currentItem = calendar.get(Calendar.DAY_OF_MONTH) - 1 + size
            }
        }
    }


    private fun initTitle() {
        val patternSb = StringBuilder()
        if (isNoLimit) {
            if (isShowYear) {
                patternSb.append("yyyy${labelYear}")
            }
            if (montIndex != 0 && isShowMonth) {
                patternSb.append("MM${labelMonth}")
            }
            if (dayIndex != 0 && isShowDay) {
                patternSb.append("dd${labelDay}")
            }
        } else {
            if (isShowYear) {
                patternSb.append("yyyy${labelYear}")
            }

            if (isShowMonth) {
                patternSb.append("MM${labelMonth}")
            }

            if (isShowDay) {
                patternSb.append("dd${labelDay}")
            }
        }
        countWeek()
        dateSb.clear()
        dateSb.append(
            SimpleDateFormat(patternSb.toString(), Locale.CHINA).format(calendar.time)
        )

        if (title.isNotEmpty()) {
            tvTitle.text = title
        } else {
            val dateStr = dateSb.toString() + weekSb.toString()
            tvTitle.text = dateStr
        }
    }

    private fun countWeek(): String {
        weekSb.clear()
        if (!isShowDay) {
            return weekSb.toString()
        }
        if (isNoLimit) {
            if (dayIndex == 0) {
                return weekSb.toString()
            }
        }
        weekSb.append(" 周")
        weekSb.append(weekStrList[calendar.get(Calendar.DAY_OF_WEEK) - 1])

        return weekSb.toString()
    }

    fun getTitle(): String = tvTitle.text.toString()


    /**
     * 显示年
     */
    fun showYear() {
        isShowYear = true
        isShowMonth = false
        isShowDay = false
        title = "选择年"
        show()
        wvYear.visibility = View.VISIBLE
        wvMonth.visibility = View.GONE
        wvDay.visibility = View.GONE
    }

    /**
     * 显示月
     */
    fun showMonth() {
        isShowYear = false
        isShowMonth = true
        isShowDay = false
        title = "选择月"
        show()
        wvYear.visibility = View.INVISIBLE
        wvMonth.visibility = View.VISIBLE
        wvDay.visibility = View.INVISIBLE
    }

    /**
     * 显示年月
     */
    fun showYearMonth() {
        isShowYear = true
        isShowMonth = true
        isShowDay = false
        title = "选择年月"
        show()
        wvYear.visibility = View.VISIBLE
        wvMonth.visibility = View.VISIBLE
        wvDay.visibility = View.GONE

    }

    override fun dismiss() {
        title = ""
        isShowYear = true
        isShowMonth = true
        isShowDay = true
        wvYear.visibility = View.VISIBLE
        wvMonth.visibility = View.VISIBLE
        wvDay.visibility = View.VISIBLE
        super.dismiss()
    }
}