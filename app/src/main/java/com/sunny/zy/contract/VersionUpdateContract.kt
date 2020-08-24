package com.sunny.zy.contract

import com.sunny.zy.base.BasePresenter
import com.sunny.zy.base.IBaseView
import com.sunny.zy.bean.VersionBean

/**
 * Desc
 * Author 张野
 * Mail zhangye98@foxmail.com
 * Date 2020/6/15 12:07
 */
interface VersionUpdateContract {

    interface View : IBaseView {
        fun showVersionUpdate(versionBean: VersionBean)
        fun noNewVersion()
        fun downLoadResult(path: String)
        fun progress(progress: Int)
    }

    abstract class Presenter(view: View) : BasePresenter<View>(view) {
        //检查版本
        abstract fun checkVersion(version: Int)

        abstract fun downLoadAPk(url: String)
    }

}