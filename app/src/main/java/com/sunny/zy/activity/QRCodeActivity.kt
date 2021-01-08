package com.sunny.zy.activity

import android.Manifest
import android.app.Activity
import android.view.View
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.QRCodeUtil
import kotlinx.android.synthetic.main.zy_frag_qr_code.*

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2021/1/4 16:54
 */
class QRCodeActivity : BaseActivity() {

    companion object {
        const val result = "qrCode"
    }

    private val qrCodeUtil: QRCodeUtil by lazy {
        QRCodeUtil {
            intent.putExtra(result, it)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }.apply {
            cameraReadyCallBack = {
                runOnUiThread {
                    hideLoading()
                    viewfinderView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun initLayout() = R.layout.zy_frag_qr_code

    override fun initView() {
        setTitleDefault("扫一扫")
        permissionsFailedIsFinish(true)
        requestPermissions(Manifest.permission.CAMERA) {
            showLoading()
            surfaceView.post {
                qrCodeUtil.open(surfaceView)
            }
        }
    }


    override fun onClickEvent(view: View) {

    }

    override fun loadData() {

    }

    override fun onClose() {
        qrCodeUtil.onClose()
    }
}