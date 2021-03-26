package com.sunny.zy.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.sunny.zy.ZyFrameStore

/**
 * Desc 单例Toast
 * Author Zy
 * Date 2019/10/25 11:03
 */
object ToastUtil {

    private var toast: Toast? = null

    private var LONG_DELAY = 3500
    private var SHORT_DELAY = 2000

    private val delay = SHORT_DELAY

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
        val delay = if (type == Toast.LENGTH_SHORT) SHORT_DELAY else LONG_DELAY
        handler.sendEmptyMessageDelayed(delay, delay.toLong())
    }

    fun show(content: String?) {
        show(content ?: "", Toast.LENGTH_SHORT)
    }

    fun show() {
        show("阿猿正在玩命开发，敬请期待...", Toast.LENGTH_LONG)
    }
}