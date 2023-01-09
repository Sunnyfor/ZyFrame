package com.sunny.zy.crop.view

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sunny.zy.R
import com.sunny.zy.crop.callback.CropBoundsChangeListener
import com.sunny.zy.crop.callback.OverlayViewChangeListener

class CropView : FrameLayout {

    var mGestureCropImageView: GestureCropImageView

    var mViewOverlay: OverlayView


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.zy_crop_view, this, true)
        mGestureCropImageView = findViewById(R.id.imageViewCrop)
        mViewOverlay = findViewById(R.id.viewOverlay)
        mViewOverlay.processStyledAttributes()
        setListenersToViews()
    }

    private fun setListenersToViews() {

        mGestureCropImageView.cropBoundsChangeListener = object : CropBoundsChangeListener {
            override fun onCropAspectRatioChanged(cropRatio: Float) {
                mViewOverlay.setTargetAspectRatio(cropRatio)
            }
        }
        mViewOverlay.overlayViewChangeListener = object : OverlayViewChangeListener {
            override fun onCropRectUpdated(cropRect: RectF) {
                mGestureCropImageView.setCropRect(cropRect)
            }
        }
    }


    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }
}