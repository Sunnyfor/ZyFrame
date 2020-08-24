package com.sunny.zy.bean

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/6/15 11:46
 */
data class VersionBean(
    var id: String,//主键
    var versionCode: Int,//版本号
    var versionNumber: String?,//版本名称
    var updateTime: String?,//更新时间 yyyy-MM-dd HH:mm:ss
    var updateUser: String?,//更新人
    var downloadLocation: String?,//下载地址
    var updateDetails: String?//更新详情
)