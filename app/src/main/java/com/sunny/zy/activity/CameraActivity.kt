package com.sunny.zy.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.util.DisplayMetrics
import android.view.View
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.preview.GalleryPreviewActivity
import com.sunny.zy.utils.CameraXUtil
import com.sunny.zy.utils.IntentManager
import com.sunny.zy.widget.CaptureButton
import kotlinx.android.synthetic.main.zy_act_camera.*

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/11 11:47
 */
class CameraActivity : BaseActivity(), GalleryPreviewActivity.OnPreviewResultCallBack {

    private var isFirst = true

    private val cameraXUtil = CameraXUtil()

    private var galleryBean: GalleryBean? = null

    override fun initLayout() = R.layout.zy_act_camera

    override fun initView() {

        hideStatusBar(false)

        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            previewView.post {
                val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
                val screenAspectRatio = CameraXUtil.aspectRatio(metrics.widthPixels, metrics.heightPixels)
                cameraXUtil.init(
                    this,
                    previewView.surfaceProvider,
                    screenAspectRatio,
                    previewView.display.rotation
                )
                cameraXUtil.startCamera()
            }
        }

        btn_take.setCaptureListener(object : CaptureButton.CaptureListener {
            override fun takePictures() {
                //拍照
                startAlphaAnimation()
                showLoading()
                cameraXUtil.takePhoto { bean ->
                    galleryBean = bean
                    IntentManager.startGalleryPreviewActivity(bean, this@CameraActivity)
                }
            }

            override fun recordShort(time: Long) {
                setTextWithAnimation("录制时间过短")
            }

            override fun recordStart() {
                cameraXUtil.takeVideo { bean ->
                    galleryBean = bean

                    IntentManager.startVideoPlayActivity(bean.uri ?: return@takeVideo) {
                        hideLoading()
                        if (it) {
                            IntentManager.cameraResultCallBack?.invoke(bean)
                            finish()
                        } else {
                            cameraXUtil.deleteMove(bean.uri ?: return@startVideoPlayActivity)
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
        IntentManager.cameraResultCallBack = null
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


    override fun onPreview(deleteList: ArrayList<GalleryBean>) {}

    override fun onSelect(resultList: ArrayList<GalleryBean>, isFinish: Boolean) {}

    override fun onCamera(isComplete: Boolean) {
        hideLoading()
        if (isComplete) {
            galleryBean?.let {
                IntentManager.cameraResultCallBack?.invoke(it)
            }
            finish()
        } else {
            cameraXUtil.deletePicture(galleryBean?.uri ?: return)
        }
    }
}