package com.sunny.zy.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.LogUtil
import com.sunny.zy.utils.QRCodeUtil
import com.sunny.zy.utils.ToastUtil
import com.sunny.zy.utils.getPreviewOutputSize
import com.sunny.zy.widget.AutoFitSurfaceView
import kotlinx.android.synthetic.main.zy_frag_qr_code.*

/**
 * Desc
 * Author ZhangYe
 * Mail zhangye98@foxmail.com
 * Date 2021/1/4 16:54
 */
class QRCodeActivity : BaseActivity() {

    companion object {
        const val resultKey = "qrCode"

        fun intent(activity: AppCompatActivity, resultCallBack: (result: String) -> Unit) {
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK){
                    resultCallBack.invoke(it.data?.getStringExtra(resultKey) ?: "")
                }
            }.launch(Intent(activity, QRCodeActivity::class.java))
        }
    }

    val qrCodeUtil: QRCodeUtil by lazy {
        QRCodeUtil().apply {
            resultCallback = {
                intent.putExtra(resultKey, it)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            cameraReadyCallBack = {
                runOnUiThread {
                    hideLoading()
                    view_placeholder.visibility = View.GONE
                    viewfinderView.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun initLayout() = R.layout.zy_frag_qr_code

    override fun initView() {
        setTitleDefault("扫一扫")
        setPermissionsCancelFinish(true)
        setPermissionsNoHintFinish(true)

        showLoading()

        requestPermissions(Manifest.permission.CAMERA) {
            fl_content.removeAllViews()
            val surfaceView = AutoFitSurfaceView(this)
            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(
                    holder: SurfaceHolder, format: Int,
                    width: Int, height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {}

                override fun surfaceCreated(holder: SurfaceHolder) {
                    // Selects appropriate preview size and configures view finder
                    if (qrCodeUtil.queryCameraId().isEmpty()) {
                        ToastUtil.show("没有匹配到摄像头")
                        return
                    }
                    val previewSize = getPreviewOutputSize(
                        surfaceView.display,
                        qrCodeUtil.characteristics ?: return,
                        SurfaceHolder::class.java
                    )
                    LogUtil.i("View finder size: ${surfaceView.width} x ${surfaceView.height}")
                    LogUtil.i("Selected preview size: $previewSize")
                    surfaceView.setAspectRatio(previewSize.width, previewSize.height)
                    surfaceView.post {
                        qrCodeUtil.open(surfaceView)
                    }
                }
            })
            fl_content.addView(surfaceView)
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