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
import com.sunny.zy.R

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020/12/21 17:00
 */
class PermissionsUtil(var requestCode: Int) {

    private var permissionOkResult: (() -> Unit)? = null

    private var permission: Array<String>? = null

    var isCancelFinish = false

    var isNoHintFinish = false

    var isNoHint = false

    fun requestPermissions(
        activity: AppCompatActivity,
        permission: Array<String>,
        permissionOkResult: (() -> Unit)? = null
    ) {
        this.permission = permission
        this.permissionOkResult = permissionOkResult
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val grantedList = arrayListOf<String>()
            permission.forEach {
                if (activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                    grantedList.add(it)
                }
            }
            if (grantedList.isEmpty()) {
                isNoHint = false
                SpUtil.get().remove("isNoHint")
                permissionOkResult?.invoke()
            } else {
                isNoHint = SpUtil.get().getBoolean("isNoHint")
                if (isNoHint) {
                    showSettingPermissionDialog(
                        activity,
                        Array(grantedList.size) { grantedList[it] }
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        activity, permission,
                        requestCode
                    )
                }
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


    fun retryRequestPermissions(activity: AppCompatActivity) {
        requestPermissions(activity, permission ?: return, permissionOkResult)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(
        activity: AppCompatActivity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode != this.requestCode) {
            return
        }

        val permissionsResult = arrayListOf<String>()
        grantResults.forEachIndexed { index, i ->
            if (i != PackageManager.PERMISSION_GRANTED) {
                permissionsResult.add(permissions[index])
            }
        }
        if (permissionsResult.isEmpty()) {
            permissionOkResult?.invoke()
        } else {
            isNoHint = !permissionsResult.all {
                activity.shouldShowRequestPermissionRationale(it)
            }
            SpUtil.get().setBoolean("isNoHint", isNoHint)
            showSettingPermissionDialog(
                activity,
                Array(permissionsResult.size) { permissionsResult[it] }
            )
        }
    }


    private fun showSettingPermissionDialog(
        activity: AppCompatActivity,
        permission: Array<String>
    ) {
        val build = AlertDialog.Builder(activity)
        build.setTitle("提示")
        val messageSb = StringBuilder()
        val pm = activity.packageManager
        permission.forEach {
            val permissionInfo = pm.getPermissionInfo(it, 0)
//            val permissionGroup = pm.getPermissionGroupInfo(permissionInfo.group ?: return, 0)
            val permissionName = permissionInfo.loadLabel(pm)
            if (!messageSb.contains(permissionName)) {
                messageSb.append("[").append(permissionName).append("]").append(" ")
            }
        }
        build.setMessage("${activity.getString(R.string.app_name)}未能获取 $messageSb 权限,此功能无法正常使用！")
        if (isNoHint) {
            build.setPositiveButton("设置") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
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
        build.setNegativeButton("取消") { _, _ ->
            if (isCancelFinish) {
                activity.finish()
            }

            if (isNoHint && isNoHintFinish) {
                activity.finish()
            }
        }
        build.setCancelable(false)
        build.show()
    }
}