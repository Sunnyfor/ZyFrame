package com.sunny.zy.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.gallery.bean.GalleryContentBean
import com.sunny.zy.preview.GalleryPreviewActivity
import com.sunny.zy.preview.VideoPlayActivity
import com.sunny.zy.utils.CameraXUtil
import com.sunny.zy.utils.ToastUtil
import com.sunny.zy.widget.CaptureButton
import kotlinx.android.synthetic.main.zy_act_camera.*

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/10/11 11:47
 */
class CameraActivity : BaseActivity() {

    private var isFirst = true

    private val cameraXUtil = CameraXUtil()


    companion object {
        fun intent(activity: AppCompatActivity, onResult: ((uri: Uri) -> Unit)) {
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.getParcelableExtra<Uri>("resultUri")
                    if (uri == null) {
                        ToastUtil.show("uri发生错误！")
                        return@registerForActivityResult
                    }
                    onResult.invoke(uri)
                }

            }.launch(Intent(activity, CameraActivity::class.java))
        }
    }

    override fun initLayout() = R.layout.zy_act_camera

    override fun initView() {

        hideStatusBar(false)

        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            cameraXUtil.init(this, previewView.surfaceProvider)
            cameraXUtil.startCamera()
        }

        btn_take.setCaptureListener(object : CaptureButton.CaptureListener {
            override fun takePictures() {
                //拍照
                startAlphaAnimation()
                showLoading()
                cameraXUtil.takePhoto { uri ->
                    GalleryPreviewActivity.intent(
                        this@CameraActivity,
                        GalleryContentBean(0, uri)
                    ) { result ->
                        hideLoading()
                        if (result) {
                            setResult(Activity.RESULT_OK, Intent().putExtra("resultUri", uri))
                            finish()
                        } else {
                            cameraXUtil.deletePicture(uri)
                        }
                    }
                }
            }

            override fun recordShort(time: Long) {
                setTextWithAnimation("录制时间过短")
            }

            override fun recordStart() {
                cameraXUtil.takeVideo {
                    VideoPlayActivity.intent(this@CameraActivity, it) { resultCode, uri ->
                        hideLoading()
                        if (resultCode == Activity.RESULT_OK) {
                            setResult(Activity.RESULT_OK, Intent().putExtra("resultUri", uri))
                            finish()
                        } else {
                            cameraXUtil.deleteMove(uri)
                        }
                    }
                }
                startAlphaAnimation()
            }

            @SuppressLint("MissingPermission")
            override fun recordEnd(time: Long) {
                cameraXUtil.stopVideo()
                startAlphaAnimation()
                showLoading()
            }

            override fun recordZoom(zoom: Float) {

            }

            override fun recordError() {
                startAlphaAnimation()
            }

        })
        setOnClickListener(iv_back, btn_take, iv_switch)
    }

    override fun loadData() {

    }

    override fun onClickEvent(view: View) {
        when (view.id) {

            R.id.iv_switch -> {
                cameraXUtil.switchCamera()
            }

            R.id.iv_back -> {
                finish()
            }
        }
    }

    override fun onClose() {
        cameraXUtil.onDestroy()
    }


    fun startAlphaAnimation() {
        if (isFirst) {
            val animatorTxtTip = ObjectAnimator.ofFloat(tv_tip, "alpha", 1f, 0f)
            animatorTxtTip.duration = 500
            animatorTxtTip.start()
            isFirst = false
        }
    }


    fun setTextWithAnimation(tip: String) {
        tv_tip.text = tip
        val animatorTxtTip = ObjectAnimator.ofFloat(tv_tip, "alpha", 0f, 1f, 1f, 0f)
        animatorTxtTip.duration = 2500
        animatorTxtTip.start()
    }
}