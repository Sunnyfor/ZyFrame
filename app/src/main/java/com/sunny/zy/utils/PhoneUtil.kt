package com.sunny.zy.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Desc 手机号校验工具类
 * Author ZY
 * Date 2022/9/16
 */
object PhoneUtil {

    /**
     * 手机号校验
     */
    fun isPhoneValid(phone: String?): Boolean {
        if (phone.isNullOrEmpty()) {
            ToastUtil.show("请输入手机号")
            return false
        }
        if (!checkMobileFormat(phone)) {
            ToastUtil.show("请输入正确的手机号")
            return false
        }
        return true
    }

    /**
     * 校验手机号格式
     */
    fun checkMobileFormat(phone: String?): Boolean {
        val reg = Regex("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9])|(19[0|9]))\\d{8}$")
        return phone?.matches(reg) ?: false
    }

    /**
     * 隐藏手机号中间四位
     */
    fun encryptPhone(phone: String): String {
        if (checkMobileFormat(phone)) {
            return phone.substring(0, 3) + "****" + phone.substring(7, phone.length)
//        return phone.replace("(\\d{3})\\d{4}(\\d{4})","$1****$2")
        }
        return ""
    }

    /**
     * 拨打电话
     */
    fun makingACall(context: Context, phone: String?) {
        if (checkMobileFormat(phone)) {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                val uri = Uri.parse("tel:$phone")
                intent.data = uri
                context.startActivity(intent)
            } catch (e: Exception) {
                ToastUtil.show("请检查设备是否有SIM卡")
            }
        } else {
            ToastUtil.show("电话号码有误")
        }
    }

}