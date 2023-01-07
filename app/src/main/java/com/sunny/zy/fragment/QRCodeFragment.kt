package com.sunny.zy.fragment

import android.Manifest
import android.util.DisplayMetrics
import android.view.View
import androidx.camera.view.PreviewView
import com.sunny.zy.R
import com.sunny.zy.base.BaseFragment
import com.sunny.zy.utils.CameraXUtil
import com.sunny.zy.utils.IntentManager

/**
 * Desc
 * Author ZY
 * Date 2023/1/7
 */
class QRCodeFragment : BaseFragment() {

    private val cameraXUtil = CameraXUtil()

    private val previewView by lazy {
        findViewById<PreviewView>(R.id.previewView)
    }

    override fun initLayout() = R.layout.zy_frag_qr_code

    override fun initView() {
        with(getBaseActivity()) {
            setPermissionsCancelFinish(true)
            setPermissionsNoHintFinish(true)
        }

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
                    getBaseActivity().finish()
                }
            }
        }
    }

    override fun loadData() {

    }

    override fun onClickEvent(view: View) {

    }

    /**
     * 是否打开手电筒
     */
    fun enableTorch(value: Boolean) {
        cameraXUtil.enableTorch(value)
    }

    override fun onClose() {
        cameraXUtil.onDestroy()
        IntentManager.qrCodeResultCallBack = null
    }
}