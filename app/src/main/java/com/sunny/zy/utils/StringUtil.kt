package com.sunny.zy.utils

import okhttp3.internal.and
import okhttp3.internal.toHexString
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Desc 字符串工具类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/6/11.
 */
object StringUtil {
    /**
     * 金额格式化
     * @param money 金额
     * @param len 小数位数
     * @return 格式后的金额
     */

    fun formatMoney(money: String?, len: Int, pattern: String? = null): String {
        if (money == null || money.isEmpty()) {
            return "0.00"
        }
        val num = money.toDouble()

        if (pattern != null) {
            val mFormat = DecimalFormat(pattern)
            mFormat.roundingMode = RoundingMode.DOWN
            return mFormat.format(num)
        }
        val mFormat = if (len == 0) {
            DecimalFormat("#")
        } else {
            val buff = StringBuffer()
            buff.append("0.")
            for (i in 0 until len) {
                buff.append("0")
            }
            DecimalFormat(buff.toString())
        }
        mFormat.roundingMode = RoundingMode.DOWN
        return mFormat.format(num)
    }


    fun insertComma(money: String?, len: Int = 2): String {
        val buff = StringBuffer()
        buff.append("###,##0")
        if (len > 0) {
            buff.append(".")
            for (i in 0 until len) {
                buff.append("0")
            }
        }
        return formatMoney(money, buff.toString())
    }

    fun formatMoney(money: String?, pattern: String) = formatMoney(money, 0, pattern)
    fun formatMoney(money: Double): String = formatMoney(money.toString(), 2)
    fun formatMoney(money: Float): String = formatMoney(money.toString(), 2)


    //根据长度生成随机字符串
    fun getRandomChar(length: Int): String {            //生成随机字符串
        val chr = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z'
        )
        val random = Random()
        val buffer = StringBuffer()
        for (i in 0 until length) {
            buffer.append(chr[random.nextInt(36)])
        }
        return buffer.toString()
    }


    /**
     *  生成指定范围的随机数
     */
    fun getRandomChar(min: Int, max: Int): String {
        return getRandomChar(Random().nextInt(max - min) + min)
    }

    /**
     * 数组转成字符串
     */
    fun bytesToHexString(byteArray: ByteArray, delimiter: String? = null): String {
        if (byteArray.isEmpty()) {
            return ""
        }
        val stringBuilder = StringBuilder()
        byteArray.forEachIndexed { index, byte ->
            val str = (byte and 0XFF).toHexString()
            if (str.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(str)
            if (delimiter != null && index < byteArray.size - 1) {
                stringBuilder.append(delimiter)
            }
        }
        return stringBuilder.toString()
    }


    /**
     * 获取当前时间戳
     */
    fun getTimeStamp(): String = (System.currentTimeMillis() / 1000).toString()

    /**
     * 根据格式获取当前时间
     */
    fun getCurrentTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        return formatTime(pattern, Date())
    }

    /**
     * 格式化时间
     */
    fun formatTime(pattern: String, date: Date): String {
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(date)
    }
}