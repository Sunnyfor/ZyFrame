package com.sunny.zy.utils

import android.widget.Toast
import com.sunny.zy.ZyFrameStore

/**
 * Desc单例 Toast
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
 * Date 2019/10/25 11:03
 */
object ToastUtil {
    private var toast: Toast? = null

    /**
     * 显示Toast
     * @param content Toast信息
     */
    fun show(content: String?, type: Int) {
        content?.let {
            toast?.cancel()
            toast = null
            toast = Toast.makeText(ZyFrameStore.getContext(), content, type).apply {
                show()
            }
        }
    }

    fun show(content: String?) {
        show(content, Toast.LENGTH_SHORT)
    }

    fun show() {
        show("阿猿正在玩命开发，敬请期待...", Toast.LENGTH_LONG)
    }

    fun showInterfaceError(code: String, msg: String) {
        LogUtil.i("error:  $msg : $code")
        show("数据请求失败，请联系相关开发人员... \n $msg : $code", Toast.LENGTH_LONG)
    }

    /**
     * 为兼容旧的网络请求框架，后期可以删除
     */
    fun showInterfaceError(msg: String) {
        showInterfaceError("接口数据返回有误", msg)
    }

}