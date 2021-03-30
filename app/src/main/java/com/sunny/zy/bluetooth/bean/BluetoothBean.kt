package com.sunny.zy.bluetooth.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2021/3/23 14:14
 */
data class BluetoothBean(
    var device: BluetoothDevice
) {
    var gatt: BluetoothGatt? = null
    var isConnect = false
    var serviceId = ""
    var writeId = ""

    fun receive(state: Int, message: String) {}

    fun close() {
        gatt?.close()
    }
}