package com.sunny.zy.widget.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.sunny.zy.bean.VersionBean

class VersionUpdateDialog(
    context: Context,
    versionBean: VersionBean,
    updateListener: (() -> Unit)?
) :
    AlertDialog.Builder(context) {
    init {
        setTitle("${versionBean.versionNumber}版本上线了")
        setMessage(versionBean.updateDetails)
        setPositiveButton("马上尝试") { _: DialogInterface, _: Int ->
            updateListener?.invoke()
        }
        setNegativeButton("忽略") { _: DialogInterface, _: Int -> }
    }
}