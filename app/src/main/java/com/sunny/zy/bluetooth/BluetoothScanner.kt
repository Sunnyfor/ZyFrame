package com.sunny.zy.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseActivity
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


object BluetoothScanner : ScanCallback() {
    private var isScan = false

    lateinit var onResult: ((result: ScanResult) -> Unit)

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        (ZyFrameStore.getContext()
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    /**
     * 启动蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScan(
        baseActivity: BaseActivity,
        serviceId: String? = null,
        onResult: ((result: ScanResult) -> Unit)
    ) {
        this.onResult = onResult
        if (bluetoothAdapter == null) {
            ToastUtil.show("当前设备不支持蓝牙！")
        }

        if (!isScan) {
            if (bluetoothAdapter?.isEnabled == false) {
                baseActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        scan(serviceId)
                    } else {
                        ToastUtil.show("请打开蓝牙后扫描！")
                    }
                }.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                return
            }
            scan(serviceId)
        } else {
            ToastUtil.show("蓝牙正在扫描中！")
        }
    }


    /**
     * 停止蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun stopScan() {
        LogUtil.i("停止蓝牙扫描！")
        isScan = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
    }


    /**
     * 蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    private fun scan(serviceId: String? = null) {
        isScan = true

        val bleScanFilters: ArrayList<ScanFilter> = ArrayList()
        //添加过滤服务ID
        serviceId?.let {
            bleScanFilters.add(
                ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(serviceId))).build()
            )
        }
        bluetoothAdapter?.bluetoothLeScanner?.startScan(
            bleScanFilters, ScanSettings.Builder().build(), this
        )
        LogUtil.i("开始蓝牙扫描！")
    }


    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        LogUtil.i("蓝牙扫描失败:$errorCode")
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        if (::onResult.isInitialized) {
            onResult.invoke(result)
        }
    }
}