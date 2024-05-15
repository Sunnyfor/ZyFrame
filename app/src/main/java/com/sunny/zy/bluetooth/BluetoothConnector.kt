package com.sunny.zy.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import com.sunny.kit.utils.application.ZyKit
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.bluetooth.bean.BluetoothBean
import java.util.UUID

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/3/22 16:28
 */
object BluetoothConnector {

    const val STATE_CONNECTED = 0 //连接
    const val STATE_DIS_CONNECT = 1 //断开连接
    const val STATE_GATT_WRITE = 2 //可写入

    fun connect(bean: BluetoothBean) {
        bean.device.connectGatt(ZyFrameStore.getContext(), false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt?, status: Int, newState: Int
            ) {
                super.onConnectionStateChange(gatt, status, newState)
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        ZyKit.log.i("连接蓝牙成功:${bean.device.address}  $status  $newState")
                        bean.isConnect = true
                        bean.gatt = gatt
                        gatt?.discoverServices()
                        bean.receiveState(STATE_CONNECTED, "连接蓝牙成功")
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        bean.isConnect = false
                        bean.gatt?.close()
                        bean.gatt = null
                        ZyKit.log.i("蓝牙设备断开:${bean.device.address}  $status")
                        bean.receiveState(STATE_DIS_CONNECT, "蓝牙设备断开")
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                ZyKit.log.i("发现服务:${status}")
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val gattService = gatt?.getService(UUID.fromString(bean.serviceId))
                    val characteristic =
                        gattService?.getCharacteristic(UUID.fromString(bean.notifyId))
                    val isOk = gatt?.setCharacteristicNotification(characteristic, true)
                    if (isOk == true) {
                        ZyKit.log.i("打开蓝牙通知!")
                        characteristic?.descriptors?.forEach {
                            val result =
                                it.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                            if (result) {
                                ZyKit.log.i("设置蓝牙写入!")
                                gatt.writeDescriptor(it)
                                bean.receiveState(STATE_GATT_WRITE, "蓝牙设备蓝牙写入")
                            }
                        }
                    }

                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                ZyKit.log.i("蓝牙数据onCharacteristicRead：${status}")
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                ZyKit.log.i("蓝牙数据onCharacteristicWrite：${status}")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                val resultByte = characteristic?.value ?: byteArrayOf()

                val resultSb = StringBuilder()
                resultByte.forEach {
                    resultSb.append(it)
                }
                bean.receiveMessage(resultByte)
                ZyKit.log.i("蓝牙数据：${resultSb}")
            }
        })
    }

    /**
     * 发送消息
     */
    fun sendMsg(bluetoothBean: BluetoothBean, byteArray: ByteArray): Boolean {
        if (!bluetoothBean.isConnect) {
            ZyKit.toast.show("设备${bluetoothBean.device.address}未连接！")
            return false
        }
        val gattService = bluetoothBean.gatt?.getService(UUID.fromString(bluetoothBean.serviceId))
        val characteristic = gattService?.getCharacteristic(UUID.fromString(bluetoothBean.writeId))
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        characteristic?.value = byteArray
//        ZyKit.log.i(
//            "${bluetoothBean.device.name}-发送数据:${StringUtil.byteArrToHex(byteArray)}"
//        )
        return bluetoothBean.gatt?.writeCharacteristic(characteristic) ?: false

    }


}