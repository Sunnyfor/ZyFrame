package com.sunny.zy.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.sunny.zy.ZyFrameStore

/**
 * Desc 单例Toast
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2019/10/25 11:03
 */
object ToastUtil {

    private var toast: Toast? = null

    private var LENGTH_LONG = 3500
    private var LENGTH_SHORT = 2000

    private val delay = LENGTH_SHORT

    private val handler = Handler(Looper.getMainLooper()) {
        toast?.cancel()
        toast = null
        return@Handler false
    }

    /**
     * 显示Toast
     * @param content Toast信息
     */
    fun show(content: String?, type: Int) {
        handler.removeMessages(delay)
        toast?.cancel()
        toast = Toast.makeText(ZyFrameStore.getContext(), content ?: "", type).apply {
            show()
        }
        val delay = if (type == Toast.LENGTH_SHORT) LENGTH_SHORT else LENGTH_LONG
        handler.sendEmptyMessageDelayed(delay, delay.toLong())
    }

    fun show(content: String?) {
        show(content ?: "", Toast.LENGTH_SHORT)
    }

    fun show() {
        show("阿猿正在玩命开发，敬请期待...", Toast.LENGTH_LONG)
    }
}