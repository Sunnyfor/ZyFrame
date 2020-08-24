package com.sunny.zy.base

import java.io.File

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/6/2 16:50
 */
data class WriteFileBean(
        var file: File,
        var downloadSize: Long,
        var fileSize: Long,
        var progress: Int
)