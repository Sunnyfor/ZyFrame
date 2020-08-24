package com.sunny.zy.widget.dialog

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.sunny.zy.R
import com.sunny.zy.http.ZyConfig
import kotlinx.android.synthetic.main.dialog_layout_download.view.*
import java.io.File

class DownLoadDialog(context: Context) : AlertDialog.Builder(context) {
    private var dialog: AlertDialog? = null

    val view: View by lazy {
        View.inflate(context, R.layout.dialog_layout_download, null)
    }

    init {
        setView(view)
        setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun show(): AlertDialog? {
        if (dialog != null) {
            dismiss()
        }
        dialog = super.show()
        dialog?.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    //设置进度
    fun setProgress(progress: Int) {
        view.tv_progress.text = (context.getString(R.string.dialogDownload) + progress + "%")
        view.progress.progress = progress
    }

    //开始安装APK
    fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(context, ZyConfig.authorities, file)
        } else {
            Uri.fromFile(file)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val resolverList = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            resolverList.forEach {
                val packageName = it.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }
        context.startActivity(intent)
    }
}