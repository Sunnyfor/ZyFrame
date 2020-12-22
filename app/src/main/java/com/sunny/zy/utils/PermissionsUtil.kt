package com.sunny.zy.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2020/12/21 17:00
 */
class PermissionsUtil(var requestCode: Int) {

    private var permissionOkResult: (() -> Unit)? = null


    fun requestPermissions(
        activity: AppCompatActivity,
        permission: Array<String>,
        permissionOkResult: (() -> Unit)? = null
    ) {
        this.permissionOkResult = permissionOkResult
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val grantedList = arrayListOf<String>()
            permission.forEach {
                if (activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                    grantedList.add(it)
                }
            }
            if (grantedList.isEmpty()) {
                permissionOkResult?.invoke()
            } else {
                ActivityCompat.requestPermissions(
                    activity, permission,
                    requestCode
                )
            }
        } else {
            permissionOkResult?.invoke()
        }
    }


    fun requestPermissions(
        activity: AppCompatActivity,
        permission: String,
        permissionOkResult: (() -> Unit)? = null
    ) {
        requestPermissions(activity, arrayOf(permission), permissionOkResult)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(
        activity: AppCompatActivity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode != requestCode) {
            return
        }

        var permissionsOk = true

        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                permissionsOk = false
                return@forEach
            }
        }
        if (permissionsOk) {
            permissionOkResult?.invoke()
        } else {
            var isNOHint = false
            permissions.forEach { permission ->
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    isNOHint = true
                }
            }
            if (isNOHint) {
                showSettingPermissionDialog(activity)
            } else {
                showSettingPermissionDialog(activity, permissions)
            }

        }
    }


    private fun showSettingPermissionDialog(
        activity: AppCompatActivity,
        permission: Array<String>? = null
    ) {
        val build = AlertDialog.Builder(activity)
        build.setTitle("提示")
        build.setMessage("当前应用缺少必要权限,该功能暂时无法使用！")
        if (permission == null) {
            build.setPositiveButton("设置") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri;
                activity.startActivity(intent)
            }
        } else {
            build.setPositiveButton("授权") { _, _ ->
                ActivityCompat.requestPermissions(
                    activity, permission,
                    requestCode
                )
            }
        }
        build.setNegativeButton("取消") { _, _ -> }
        build.show()
    }
}