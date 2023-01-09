package com.sunny.zy.activity

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sunny.kit.utils.FileUtil
import com.sunny.kit.utils.LogUtil
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.base.BasePresenter
import com.sunny.zy.base.IBaseView
import com.sunny.zy.base.bean.MenuBean
import com.sunny.zy.crop.view.CropView
import com.sunny.zy.crop.view.HorizontalProgressWheelView
import com.sunny.zy.crop.view.TransformImageView
import com.sunny.zy.gallery.bean.GalleryBean
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class CropActivity : BaseActivity(), TransformImageView.TransformImageListener {

    private val cropView by lazy {
        findViewById<CropView>(R.id.cropView)
    }

    private val flReset by lazy {
        findViewById<View>(R.id.flReset)
    }

    private val flRotate by lazy {
        findViewById<View>(R.id.flRotate)
    }

    private val hpv by lazy {
        findViewById<HorizontalProgressWheelView>(R.id.hpv)
    }

    private val tvRotate by lazy {
        findViewById<TextView>(R.id.tvRotate)
    }

    private val presenter by lazy {
        object : BasePresenter<IBaseView>(this) {}
    }

    override fun initLayout() = R.layout.zy_act_crop

    override fun initView() {

        val menuBean = MenuBean("确定") {
            presenter.launch {
                val resultFile = cropView.mGestureCropImageView.saveImage()
                LogUtil.i("裁剪完成：${resultFile?.path}")
                finish()
            }
        }
        toolbar.setTitleDefault("裁剪", menuBean)
        setImmersionResource(R.color.preview_bg)

        val data = intent.getParcelableExtra<GalleryBean>("data")
        settingView()
        val uri = data?.uri
        if (uri == null) {
            finish()
            return
        }
        val inputStream = contentResolver.openInputStream(uri)

        val privateFile = File(FileUtil.getExternalDir("gallery"), data.name)
        if (privateFile.exists()) {
            privateFile.delete()
        }
        inputStream?.let {
            privateFile.outputStream().write(it.readBytes())
            it.close()
        }
        cropView.mGestureCropImageView.initImageFile(privateFile)
        cropView.mGestureCropImageView.setTransformImageListener(this)

        setOnClickListener(flReset, flRotate)
    }

    override fun loadData() {

    }

    override fun onClickEvent(view: View) {
        when (view.id) {
            R.id.flReset -> {
                with(cropView) {
                    mGestureCropImageView.postRotate(-mGestureCropImageView.currentAngle)
                    mGestureCropImageView.setImageToWrapCropBounds()
                }

            }
            R.id.flRotate -> {
                rotateByAngle()
            }
        }
    }

    private fun settingView() {
        with(cropView.mGestureCropImageView) {
            isRotateEnabled = false
            targetAspectRatio = 1F
        }

        with(cropView.mViewOverlay) {
            setShowCropFrame(true)
            setCropFrameStrokeWidth(resources.getDimensionPixelSize(R.dimen.dp_2))
            setCropFrameColor(ContextCompat.getColor(context, R.color.color_white))
            setDimmedColor(ContextCompat.getColor(context, R.color.dialog_bg))
        }

        hpv.setScrollingListener(object : HorizontalProgressWheelView.ScrollingListener {
            override fun onScrollStart() {
                cropView.mGestureCropImageView.cancelAllAnimations()
            }

            override fun onScroll(delta: Float, totalDistance: Float) {
                val value = (delta / 42)
                cropView.mGestureCropImageView.postRotate(value)
            }

            override fun onScrollEnd() {
                cropView.mGestureCropImageView.setImageToWrapCropBounds()
            }

        })
    }

    private fun rotateByAngle() {
        with(cropView) {
            mGestureCropImageView.postRotate(90F)
            mGestureCropImageView.setImageToWrapCropBounds()
        }
    }

    override fun onLoadComplete() {
        cropView.animate().alpha(1f).setDuration(300).interpolator = AccelerateInterpolator()
    }

    override fun onLoadFailure(e: Exception) {}

    override fun onRotate(currentAngle: Float) {
        var value = currentAngle
        if (value == -0.0f) {
            value = 0.0f
        }
        val angle = String.format(Locale.getDefault(), "%.1f°", value)
        tvRotate.text = angle
    }

    override fun onScale(currentScale: Float) {}


    override fun onClose() {

    }

    fun cropAndSaveImage() {
        showLoading()

    }
}