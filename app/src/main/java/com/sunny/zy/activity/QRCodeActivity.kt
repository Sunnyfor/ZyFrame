package com.sunny.zy.activity

import android.view.View
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.fragment.QRCodeFragment


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/1/4 16:54
 */
class QRCodeActivity : BaseActivity() {

    override fun initLayout() = QRCodeFragment()

    override fun initView() {
        setTitleDefault("扫一扫")
    }


    override fun onClickEvent(view: View) {}

    override fun loadData() {}

    override fun onClose() {}
}