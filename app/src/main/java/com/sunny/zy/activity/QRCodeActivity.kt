package com.sunny.zy.activity

import android.Manifest
import android.util.DisplayMetrics
import android.view.View
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.CameraXUtil
import com.sunny.zy.utils.IntentManager
import kotlinx.android.synthetic.main.zy_frag_qr_code.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/1/4 16:54
 */
class QRCodeActivity : BaseActivity() {

    private val cameraXUtil = CameraXUtil()

    override fun initLayout() = R.layout.zy_frag_qr_code

    override fun initView() {
        setTitleDefault("扫一扫")
        setPermissionsCancelFinish(true)
        setPermissionsNoHintFinish(true)

        requestPermissions(Manifest.permission.CAMERA) {
            previewView.post {
                val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
                val screenAspectRatio =
                    CameraXUtil.aspectRatio(metrics.widthPixels, metrics.heightPixels)
                cameraXUtil.init(
                    this,
                    previewView.surfaceProvider,
                    screenAspectRatio,
                    previewView.display.rotation
                )
                cameraXUtil.startQrCodeScan {
                    IntentManager.qrCodeResultCallBack?.invoke(it)
                    finish()
                }
            }
        }
    }


    override fun onClickEvent(view: View) {

    }

    override fun loadData() {

    }

    override fun onClose() {
        cameraXUtil.onDestroy()
        IntentManager.qrCodeResultCallBack = null
    }
}