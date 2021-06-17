package com.sunny.zy.host

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.sunny.zy.R
import com.sunny.zy.utils.ToastUtil

/**
 * Desc 域名配置入口设置彩蛋
 * Author ZY
 * Mail zuoyu98@foxmail.com
 * Date 2021/6/3 10:37
 */
object HostSetUtil {

    /**
     * 存放点击事件次数
     */
    private var mHits: LongArray? = null

    /**
     * 测试彩蛋
     */
    fun displayEggs(context: Context) {
        if (mHits == null) {
            mHits = LongArray(3) // 需要点击几次 就设置几
        }

        mHits?.let {
            //把从第二位至最后一位之间的数字复制到第一位至倒数第一位
            System.arraycopy(mHits ?: longArrayOf(), 1, mHits, 0, it.size - 1)

            //记录一个时间
            it[it.size - 1] = SystemClock.uptimeMillis()
            if (SystemClock.uptimeMillis() - it[0] <= 5000) {//5秒内连续点击。
                mHits = null    //这里说明一下，我们在进来以后需要还原状态，否则如果点击过快，第六次，第七次 都会不断进来触发该效果。重新开始计数即可

                val isApkInDebug =
                    context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
                if (isApkInDebug) {
                    HostSetDialog(context).show()
                } else {
                    showAdminDialog(context)
                }
            }
        }
    }

    /**
     * 展示管理员权限对话框
     */
    private fun showAdminDialog(context: Context) {

        val view = View.inflate(context, R.layout.dialog_host_pwd, null)
        val dialog = AlertDialog.Builder(context)
            .setTitle("温馨提示")
            .setView(view)
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.show()

        val cancelBtn = view.findViewById<Button>(R.id.btn_cancel)
        val confirmBtn = view.findViewById<Button>(R.id.btn_confirm)
        val inputEditText = view.findViewById<EditText>(R.id.et_input)
        inputEditText.hint = "请输入暗号"
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        confirmBtn.setOnClickListener {
            if ("贞和科技" == inputEditText.text.toString()) {
                HostSetDialog(context).show()
            } else {
                ToastUtil.show("身份验证失败")
            }
            dialog.dismiss()
        }
    }

}