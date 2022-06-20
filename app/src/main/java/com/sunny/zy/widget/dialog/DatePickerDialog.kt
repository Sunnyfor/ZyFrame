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
import com.sunny.zy.widget.wheel.WheelView
import com.sunny.zy.widget.wheel.listener.OnItemSelectedListener
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
    BaseDialog(context) {

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

    private val wvYear by lazy {
        findViewById<WheelView>(R.id.wvYear)
    }

    private val wvMonth by lazy {
        findViewById<WheelView>(R.id.wvMonth)
    }

    private val wvDay by lazy {
        findViewById<WheelView>(R.id.wvDay)
    }

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


    override fun initLayout() = R.layout.dialog_date_select

    override fun initView() {
        window?.setGravity(Gravity.BOTTOM)
        setWheelViewStyle(wvYear)
        wvYear.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                val pattern = Pattern.compile("^[0-9]*")
                val matcher = pattern.matcher(years[index])
                matcher.find()
                calendar.set(Calendar.YEAR, matcher.group().toInt())
                initDay()
                initTitle()
            }
        })

        setWheelViewStyle(wvMonth)
        wvMonth.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                montIndex = index
                if (isNoLimit && index == 0) {
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    initDay()
                    initTitle()
                    return
                }

                val position = if (isNoLimit) 1 else 0

                val mCalendar = Calendar.getInstance()
                mCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                mCalendar.set(Calendar.DAY_OF_MONTH, 1)
                mCalendar.set(Calendar.MONTH, index - position)

                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val maxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (currentDay >= maxDay) {
                    calendar.set(Calendar.DAY_OF_MONTH, maxDay)
                }

                calendar.set(Calendar.MONTH, index - position)
                initDay()
                initTitle()
            }
        })

        setWheelViewStyle(wvDay)

        wvDay.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                dayIndex = index
                if (isNoLimit && index == 0) {
                    initTitle()
                    return
                }
                val position = if (isNoLimit) 1 else 0
                calendar.set(Calendar.DAY_OF_MONTH, index + 1 - position)
                initTitle()
            }

        })


        findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dismiss()
        }

        findViewById<TextView>(R.id.tvConfirm).setOnClickListener {
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

    override fun loadData() {}

    override fun onClickEvent(view: View) {}

    override fun onClose() {}


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
        wvYear.setAdapter(ArrayWheelAdapter(years))
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
            wvMonth.setAdapter(ArrayWheelAdapter(months))
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
            wvDay.setAdapter(ArrayWheelAdapter(days))
            dayIndex = 0
            wvDay.currentItem = dayIndex
        } else {
            for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                days.add("$i$labelDay")
            }
            wvDay.setAdapter(ArrayWheelAdapter(days))
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

        findViewById<TextView>(R.id.tvTitle).text = if (title.isNotEmpty()) {
            title
        } else {
            dateSb.toString() + weekSb.toString()
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

    fun getTitle(): String = findViewById<TextView>(R.id.tvTitle).text.toString()


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

    private fun setWheelViewStyle(wheelView: WheelView) {
        wheelView.setTextSize(20f)
        wheelView.setTypeface(Typeface.DEFAULT)
        wheelView.setDividerColor(ContextCompat.getColor(context, R.color.color_transparent))
        wheelView.setItemsVisibleCount(5)
        wheelView.setLineSpacingMultiplier(2f)
    }
}