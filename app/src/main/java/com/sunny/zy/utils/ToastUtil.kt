package com.sunny.zy.utils

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.sunny.zy.BuildConfig
import com.sunny.zy.ZyFrameStore

/**
 * Desc 单例Toast
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2019/10/25 11:03
 */
object ToastUtil {

    private var toast: Toast? = null

    private var LENGTH_LONG = 7000
    private var LENGTH_SHORT = 4000

    private val delay = LENGTH_SHORT

    private val handler = Handler(Looper.getMainLooper()) {
        toast?.cancel()
        toast = null
        return@Handler false
    }

    private val layoutRes by lazy {
        ZyFrameStore.getContext().resources.getIdentifier("transient_notification", "layout", "android")
    }

    /**
     * 显示Toast
     * @param content Toast信息
     */
    fun show(content: String?, duration: Int, gravity: Int) {
        if (content?.isEmpty() == true) return
        handler.removeMessages(delay)
        toast?.cancel()

        val view = LayoutInflater.from(ZyFrameStore.getContext()).inflate(layoutRes, null)
        val textView = view.findViewById<TextView>(android.R.id.message)
        textView.text = content
        toast = Toast(ZyFrameStore.getContext())
        toast?.view = view
        toast?.duration = duration

        if (gravity != 0) {
            toast?.setGravity(gravity, 0, 0)
        }
        toast?.show()

        val delay = if (duration == Toast.LENGTH_SHORT) LENGTH_SHORT else LENGTH_LONG
        handler.sendEmptyMessageDelayed(delay, delay.toLong())
    }

    /**
     * Toast底部显示
     * @param content 打印内容
     * @param duration 打印长短
     */
    fun show(content: String?, duration: Int) {
        show(content, duration, 0)
    }

    /**
     * Toast底部显示
     */
    fun show(content: String?) {
        show(content, Toast.LENGTH_SHORT)
    }

    /**
     * Toast居中显示
     * @param content 打印内容
     * @param duration 打印长短
     */
    fun showCenter(content: String?, duration: Int) {
        show(content, duration, Gravity.CENTER)
    }

    /**
     * Toast居中显示
     */
    fun showCenter(content: String?) {
        showCenter(content, Toast.LENGTH_SHORT)
    }

    /**
     * Toast顶部显示
     * @param content 打印内容
     * @param duration 打印长短
     */
    fun showTop(content: String?, duration: Int) {
        show(content, duration, Gravity.TOP)
    }

    /**
     * Toast顶部显示
     */
    fun showTop(content: String?) {
        showTop(content, Toast.LENGTH_SHORT)
    }

    /**
     * 仅Debug包打印
     */
    fun showDebug(content: String?) {
        if (BuildConfig.DEBUG) {
            show(content)
        }
    }

}