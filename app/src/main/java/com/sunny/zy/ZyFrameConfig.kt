package com.sunny.zy

import android.util.Log

/**
 * Desc 框架全局配置清单
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2017/10/12.
 */
object ZyFrameConfig {

    /**
     * 设置Glide的日志级别
     */
    var glideLogLevel = Log.ERROR

    /**
     * provider权限
     */
    var authorities = "com.sunny.zy.provider"

    /**
     * 设置StatusBar文字颜色
     */
    var statusBarIsDark = false

}