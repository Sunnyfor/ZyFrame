package com.sunny.zy.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.LogUtil
import com.sunny.zy.utils.ToastUtil
import java.util.*
import kotlin.collections.ArrayList


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/3/22 15:22
 */
object ZyBluetoothScanner {
    private var isScan = false

    private var permissions = arrayOf(
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private var onLeScanCallback: ScanCallback? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        (ZyFrameStore.getContext()
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private var receiver: BroadcastReceiver? = null

    /**
     * 启动蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startLeScan(
        activity: BaseActivity,
        filters: ArrayList<ScanFilter>? = null,
        settings: ScanSettings? = null,
        onResult: ScanCallback? = null
    ) {
        this.onLeScanCallback = onResult
        if (bluetoothAdapter == null) {
            ToastUtil.show("当前设备不支持蓝牙！")
        }

        if (isScan) {
            ToastUtil.show("蓝牙正在扫描中！")
            return
        }
        checkBlueTooth(activity) {
            startLeScan(activity, filters, settings)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startLeScan(
        activity: BaseActivity,
        onResult: ScanCallback? = null
    ) {
        startLeScan(activity, arrayListOf(), null, onResult)
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startLeScan(
        activity: BaseActivity,
        serviceIds: ArrayList<String>,
        onResult: ScanCallback? = null
    ) {
        val bleScanFilters: ArrayList<ScanFilter> = ArrayList()
        //添加过滤服务ID
        if (serviceIds.isNotEmpty()) {
            serviceIds.forEach {
                bleScanFilters.add(
                    ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(it))).build()
                )
            }
        }
        startLeScan(activity, bleScanFilters, null, onResult)
    }


    /**
     * 低功耗停止蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun stopLeScan() {
        LogUtil.i("停止蓝牙扫描！")
        isScan = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(onLeScanCallback)
    }

    /**
     * 经典蓝牙开始扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScan(activity: BaseActivity, callback: ScanResultCallback) {

        if (isScan) {
            return
        }

        activity.requestPermissions(permissions) {
            isScan = true
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    when (intent.action) {
                        BluetoothDevice.ACTION_FOUND -> {
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                                ?.let {
                                    callback.onScanResult(it)
                                }
                        }
                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                            callback.onScanFinish()
                            isScan = false
                        }
                    }
                }
            }
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            activity.registerReceiver(receiver, filter)
            bluetoothAdapter?.startDiscovery()
        }
    }

    /**
     * 经典蓝牙停止扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun stopScan(activity: AppCompatActivity) {
        isScan = false
        activity.unregisterReceiver(receiver ?: return)
        bluetoothAdapter?.cancelDiscovery()
    }


    /**
     * 低功耗蓝牙扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    private fun startLeScan(
        activity: BaseActivity,
        filters: ArrayList<ScanFilter>? = null,
        settings: ScanSettings? = null
    ) {
        activity.requestPermissions(permissions) {
            isScan = true
            bluetoothAdapter?.bluetoothLeScanner?.startScan(
                filters, settings ?: ScanSettings.Builder().build(), onLeScanCallback
            )
        }
        LogUtil.i("开始蓝牙扫描！")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    private fun checkBlueTooth(activity: BaseActivity, callback: () -> Unit) {
        if (bluetoothAdapter?.isEnabled == false) {
            activity.hideLoading()
            ToastUtil.show("请打开蓝牙后扫描！")
        } else {
            callback.invoke()
        }
    }

    interface ScanResultCallback {
        //扫描结果
        fun onScanResult(devices: BluetoothDevice)

        //扫描结束
        fun onScanFinish()
    }
}