package com.sunny.zy

import android.util.Log
import com.sunny.zy.base.ICreateStateView
import com.sunny.zy.widget.DefaultCreateStateView

/**
 * Desc 框架全局配置清单
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2017/10/12.
 */
object ZyFrameConfig {
    /**
     * 是否打印LOG
     */
    var isPrintLog = true

    /**
     * 设置Log标签名
     */
    var logTag = "ZYLog"

    /**
     * 设置Glide的日志级别
     */
    var glideLogLevel = Log.ERROR

    /**
     * provider权限
     */
    var authorities = "com.sunny.zy.provider"

    /**
     * 两次点击事件间隔，单位毫秒
     */
    var clickInterval = 500L

    /**
     * 内存卡缓存路径
     */
    var TEMP = ZyFrameStore.getContext().getExternalFilesDir("temp")?.path ?: ""

    /**
     * 设置StatusBar文字颜色
     */
    var statusBarIsDark = false


    /**
     * 全局创建状态覆盖层View
     */
    var createStateView: ICreateStateView = DefaultCreateStateView()

}