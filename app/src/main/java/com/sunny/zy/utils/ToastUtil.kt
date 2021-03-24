package com.sunny.zy.utils

import android.widget.Toast
import com.sunny.zy.ZyFrameStore

/**
 * Desc 单例Toast
 * Author Zy
 * Date 2019/10/25 11:03
 */
object ToastUtil {

    private var toast: Toast? = null

    /**
     * 显示Toast
     * @param content Toast信息
     */
    fun show(content: String?, type: Int) {
        toast?.cancel()
        toast = null
        toast = Toast.makeText(ZyFrameStore.getContext(), content ?: "", type).apply {
            show()
        }
    }

    fun show(content: String?) {
        show(content ?: "", Toast.LENGTH_SHORT)
    }

    fun show() {
        show("阿猿正在玩命开发，敬请期待...", Toast.LENGTH_LONG)
    }
}