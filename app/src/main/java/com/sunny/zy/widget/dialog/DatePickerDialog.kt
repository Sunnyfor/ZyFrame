package com.sunny.zy.widget.dialog

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sunny.kit.utils.StringUtil
import com.sunny.zy.R
import com.sunny.zy.adapter.ArrayWheelAdapter
import com.sunny.zy.base.widget.dialog.BaseDialog
import com.sunny.zy.widget.wheel.WheelView
import com.sunny.zy.widget.wheel.listener.OnItemSelectedListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class DatePickerDialog(context: Context, var resultCallback: (date: String) -> Unit) :
    BaseDialog(context) {

    private val years = arrayListOf<String>()
    private val months = arrayListOf<String>()
    private val days = arrayListOf<String>()
    private val hours = arrayListOf<String>()
    private val minutes = arrayListOf<String>()
    private val weekStrList = arrayOf("日", "一", "二", "三", "四", "五", "六")

    private val calendar by lazy {
        Calendar.getInstance()
    }

    var labelYear = "年"
    var labelMonth = "月"
    var labelWeek = "周"
    var labelDay = "日"
    var labelHour = "时"
    var labelMinute = "分"
    var labelDate = "-" //日期分隔符
    var labelSplit = " "//日期和时间的分隔符
    var labelTime = ":" //时间分割符
    var isFillSecond = false //是否已00填充秒

    private var dateSb = StringBuilder()
    private val weekSb = StringBuilder()
    private var resultSb = StringBuilder()

    private val tvTitle by lazy {
        findViewById<TextView>(R.id.tvTitle)
    }

    private val wvYear by lazy {
        findViewById<WheelView>(R.id.wvYear)
    }

    private val wvMonth by lazy {
        findViewById<WheelView>(R.id.wvMonth)
    }

    private val wvDay by lazy {
        findViewById<WheelView>(R.id.wvDay)
    }


    private val wvHour by lazy {
        findViewById<WheelView>(R.id.wvHour)
    }

    private val wvMinute by lazy {
        findViewById<WheelView>(R.id.wvMinute)
    }

    private var montIndex = 1

    private var dayIndex = 1

    private var isShowYear = false

    private var isShowMonth = false

    private var isShowDay = false

    private var isShowHour = false

    private var isShowMinute = false

    private var title = ""

    override fun initLayout() = R.layout.dialog_date_time_select

    override fun initView() {
        window?.setGravity(Gravity.BOTTOM)
        wvYear.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                val pattern = Pattern.compile("^[0-9]*")
                val matcher = pattern.matcher(years[index])
                matcher.find()
                calendar.set(Calendar.YEAR, matcher.group().toInt())
                if (isShowDay) {
                    loadDay()
                }
                initTitle()
            }
        })

        wvMonth.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                montIndex = index
                val mCalendar = Calendar.getInstance()
                mCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                mCalendar.set(Calendar.DAY_OF_MONTH, 1)
                mCalendar.set(Calendar.MONTH, index)
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val maxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (currentDay >= maxDay) {
                    calendar.set(Calendar.DAY_OF_MONTH, maxDay)
                }
                calendar.set(Calendar.MONTH, index)
                if (isShowDay) {
                    loadDay()
                }
                initTitle()
            }
        })

        wvDay.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                dayIndex = index
                calendar.set(Calendar.DAY_OF_MONTH, index + 1)
                initTitle()
            }
        })

        wvYear.setLabel(labelYear)
        wvMonth.setLabel(labelMonth)
        wvDay.setLabel(labelDay)
        wvHour.setLabel(labelHour)
        wvMinute.setLabel(labelMinute)

        setWheelViewStyle(wvYear)
        setWheelViewStyle(wvMonth)
        setWheelViewStyle(wvDay)
        setWheelViewStyle(wvHour)
        setWheelViewStyle(wvMinute)


        findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dismiss()
        }

        findViewById<TextView>(R.id.tvConfirm).setOnClickListener {
            resultSb.clear()
            if (isShowYear) {
                resultSb.append(getValueStr(years[wvYear.currentItem]))
            }
            if (isShowMonth) {
                addLabel(labelDate)
                resultSb.append(getValueStr(months[wvMonth.currentItem]))
            }

            if (isShowDay) {
                addLabel(labelDate)
                resultSb.append(getValueStr(days[wvDay.currentItem]))
            }

            if (isShowHour) {
                addLabel(labelSplit)
                resultSb.append(getValueStr(hours[wvHour.currentItem]))
            }

            if (isShowMinute) {
                addLabel(labelTime)
                resultSb.append(getValueStr(minutes[wvMinute.currentItem]))
            }

            if (isShowHour && isShowMinute && isFillSecond) {
                resultSb.append(labelTime).append("00")
            }

            resultCallback.invoke(resultSb.toString())
            dismiss()
        }
    }

    override fun loadData() {
        loadDateData()
        loadTimeData()
        initTitle()
    }


    private fun initTitle() {
        if (isShowYear && isShowMonth && isShowDay) {
            val patternSb = StringBuilder()
            patternSb.append("yyyy${labelYear}")
            patternSb.append("MM${labelMonth}")
            patternSb.append("dd${labelDay}")
            countWeek()
            dateSb.clear()
            dateSb.append(
                SimpleDateFormat(patternSb.toString(), Locale.CHINA).format(calendar.time)
            )
            title = (dateSb.toString() + weekSb.toString())
        } else {
            tvTitle.visibility = View.GONE
        }
        tvTitle.text = title
    }

    private fun countWeek(): String {
        weekSb.clear()
        if (!isShowDay) {
            return weekSb.toString()
        }

        weekSb.append(labelWeek)
        weekSb.append(weekStrList[calendar.get(Calendar.DAY_OF_WEEK) - 1])

        return weekSb.toString()
    }

    private fun setWheelViewStyle(wheelView: WheelView) {
        wheelView.isCenterLabel(false)
        wheelView.setTextSize(20f)
        wheelView.setTypeface(Typeface.DEFAULT)
        wheelView.setDividerColor(ContextCompat.getColor(context, R.color.color_transparent))
        wheelView.setItemsVisibleCount(5)
        wheelView.setLineSpacingMultiplier(2f)
    }

    private fun loadDateData() {
        /**
         * 初始化年数据
         */
        if (years.isNotEmpty()) {
            return
        }
        val currentYear = calendar.get(Calendar.YEAR)
        years.clear()
        val currentYearStr = "$currentYear"
        years.add(currentYearStr)
        for (i in 1..100) {
            years.add("${currentYear + i}")
            years.add(0, "${currentYear - i}")
        }
        wvYear.setAdapter(ArrayWheelAdapter(years))
        wvYear.currentItem = years.indexOf(currentYearStr)

        /**
         *  初始化月数据
         */
        if (months.isEmpty()) {
            for (i in 1..12) {
                months.add("$i")
            }
            wvMonth.setAdapter(ArrayWheelAdapter(months))
        }
        wvMonth.currentItem = calendar.get(Calendar.MONTH)

        /**
         *  初始化日数据
         */
        loadDay()
    }

    private fun loadTimeData() {
        for (i in 0..59) {
            if (i < 24) {
                hours.add(i.toString())
            }
            minutes.add(i.toString())
        }

        wvHour.setAdapter(ArrayWheelAdapter(hours))
        wvMinute.setAdapter(ArrayWheelAdapter(minutes))

        val currentTime = StringUtil.getCurrentTime("HH:mm")
        val times = currentTime.split(":")
        wvHour.currentItem = hours.indexOf(times[0])
        wvMinute.currentItem = minutes.indexOf(times[1])
    }

    private fun loadDay() {
        /**
         * 初始化天数据
         */
        days.clear()
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            days.add("$i")
        }
        wvDay.setAdapter(ArrayWheelAdapter(days))
        if (dayIndex != 0) {
            wvDay.currentItem = calendar.get(Calendar.DAY_OF_MONTH) - 1
        }
    }

    private fun getLabelText(value: Int, label: String): String {
        return "${getValueStr(value)}$label"
    }

    private fun getValueStr(value: Int): String {
        if (value < 10) {
            return "0$value"
        }
        return value.toString()
    }

    private fun getValueStr(value: String): String {
        return getValueStr(value.toInt())
    }

    private fun addLabel(label: String) {
        if (resultSb.isNotEmpty()) {
            resultSb.append(label)
        }
    }

    override fun onClickEvent(view: View) {
        TODO("Not yet implemented")
    }

    fun showYY() {
        resetShow()
        isShowYear = true
        show()
    }

    fun showMM() {
        resetShow()
        isShowMonth = true
        show()
    }

    fun showYYMM() {
        resetShow()
        isShowYear = true
        isShowMonth = true
        show()
    }


    fun showYYMMDD() {
        resetShow()
        isShowYear = true
        isShowMonth = true
        isShowDay = true
        show()
    }

    fun showYYMMDDHHmm() {
        resetShow()
        isShowYear = true
        isShowMonth = true
        isShowDay = true
        isShowHour = true
        isShowMinute = true
        show()
    }

    fun showHHmm() {
        resetShow()
        isShowHour = true
        isShowMinute = true
        show()
    }

    override fun show() {
        super.show()
        wvYear.visibility = isShowView(isShowYear)
        wvMonth.visibility = isShowView(isShowMonth)
        wvDay.visibility = isShowView(isShowDay)
        wvHour.visibility = isShowView(isShowHour)
        wvMinute.visibility = isShowView(isShowMinute)
    }

    private fun isShowView(isShow: Boolean) = if (isShow) View.VISIBLE else View.GONE

    private fun resetShow() {
        isShowYear = false
        isShowMonth = false
        isShowDay = false
        isShowHour = false
        isShowMinute = false
    }


    override fun onClose() {
        TODO("Not yet implemented")
    }
}