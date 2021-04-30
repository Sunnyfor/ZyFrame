package com.sunny.zy.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.utils.LogUtil
import com.sunny.zy.utils.ToastUtil
import java.util.*
import kotlin.collections.ArrayList


/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2021/3/22 15:22
 */


object BluetoothScanner {
    private var isScan = false

    var onResult: ScanCallback? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        (ZyFrameStore.getContext()
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    /**
     * 启动蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScan(
        activity: AppCompatActivity,
        filters: ArrayList<ScanFilter>? = null,
        settings: ScanSettings? = null,
        onResult: ScanCallback? = null
    ) {
        this.onResult = onResult
        if (bluetoothAdapter == null) {
            ToastUtil.show("当前设备不支持蓝牙！")
        }

        if (!isScan) {
            if (bluetoothAdapter?.isEnabled == false) {
                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        scan(filters, settings)
                    } else {
                        ToastUtil.show("请打开蓝牙后扫描！")
                    }
                }.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                return
            }
            scan(filters, settings)
        } else {
            ToastUtil.show("蓝牙正在扫描中！")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScan(
        activity: AppCompatActivity,
        filters: ArrayList<ScanFilter>? = null,
        onResult: ScanCallback? = null
    ) {
        startScan(activity, filters,null, onResult)
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScan(
        activity: AppCompatActivity,
        serviceId: String? = null,
        onResult: ScanCallback? = null
    ) {
        val bleScanFilters: ArrayList<ScanFilter> = ArrayList()
        //添加过滤服务ID
        serviceId?.let {
            bleScanFilters.add(
                ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(serviceId))).build()
            )
        }
        startScan(activity, bleScanFilters, null, onResult)
    }


    /**
     * 停止蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun stopScan() {
        LogUtil.i("停止蓝牙扫描！")
        isScan = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(onResult)
    }


    /**
     * 蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    private fun scan(filters: ArrayList<ScanFilter>? = null, settings: ScanSettings? = null) {
        isScan = true
        bluetoothAdapter?.bluetoothLeScanner?.startScan(
            filters, settings ?: ScanSettings.Builder().build(), onResult
        )
        LogUtil.i("开始蓝牙扫描！")
    }
}