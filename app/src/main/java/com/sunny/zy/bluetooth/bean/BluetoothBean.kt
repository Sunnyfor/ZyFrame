package com.sunny.zy.bluetooth.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
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

    abstract fun receiveState(state: Int, message: String)

    abstract fun receiveMessage(byteArray: ByteArray)

    fun close() {
        gatt?.close()
    }

    override fun equals(other: Any?): Boolean {
        if (other is BluetoothBean) {
            if (other.device.name == device.name && other.device.address == device.address) {
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int {
        var result = device.name.hashCode() + device.address.hashCode()
        result = 31 * result + serviceId.hashCode()
        result = 31 * result + notifyId.hashCode()
        result = 31 * result + writeId.hashCode()
        result = 31 * result + (gatt?.hashCode() ?: 0)
        result = 31 * result + isConnect.hashCode()
        return result
    }
}