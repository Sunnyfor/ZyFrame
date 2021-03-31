package com.sunny.zy.bluetooth.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2021/3/23 14:14
 */
abstract class BluetoothBean(
    var device: BluetoothDevice,
    var serviceId: String,
    var notifyId: String,
    var writeId: String
) {
    var gatt: BluetoothGatt? = null
    var isConnect = false

    abstract fun receive(state: Int, message: String)

    fun close() {
        gatt?.close()
    }
}