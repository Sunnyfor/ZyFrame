package com.sunny.zy.crop.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import com.sunny.zy.crop.util.RotationGestureDetector
import com.sunny.zy.crop.util.RotationGestureDetector.SimpleOnRotationGestureListener
import kotlin.math.pow

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
open class GestureCropImageView : CropImageView {
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mRotateDetector: RotationGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null
    private var mMidPntX = 0f
    private var mMidPntY = 0f
    var isRotateEnabled = true
    var isScaleEnabled = true
    var isGestureEnabled = true
    var doubleTapScaleSteps = 5

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init {
        setupGestureListeners()
    }

    /**
     * If it's ACTION_DOWN event - user touches the screen and all current animation must be canceled.
     * If it's ACTION_UP event - user removed all fingers from the screen and current image position must be corrected.
     * If there are more than 2 fingers - update focal point coordinates.
     * Pass the event to the gesture detectors if those are enabled.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
            cancelAllAnimations()
        }
        if (event.pointerCount > 1) {
            mMidPntX = (event.getX(0) + event.getX(1)) / 2
            mMidPntY = (event.getY(0) + event.getY(1)) / 2
        }
        if (isGestureEnabled) {
            mGestureDetector!!.onTouchEvent(event)
        }
        if (isScaleEnabled) {
            mScaleDetector!!.onTouchEvent(event)
        }
        if (isRotateEnabled) {
            mRotateDetector!!.onTouchEvent(event)
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            setImageToWrapCropBounds()
        }
        return true
    }

    /**
     *
     * This method calculates target scale value for double tap gesture.
     * User is able to zoom the image from min scale value
     * to the max scale value with [.mDoubleTapScaleSteps] double taps.
     */
    protected val doubleTapTargetScale: Float
        get() = currentScale * (maxScale / minScale).toDouble()
            .pow((1.0f / doubleTapScaleSteps).toDouble()).toFloat()

    private fun setupGestureListeners() {
        mGestureDetector = GestureDetector(context, GestureListener(), null, true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mRotateDetector = RotationGestureDetector(RotateListener())
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            postScale(detector.scaleFactor, mMidPntX, mMidPntY)
            return true
        }
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            zoomImageToPosition(doubleTapTargetScale, e.x, e.y)
            return super.onDoubleTap(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            postTranslate(-distanceX, -distanceY)
            return true
        }
    }

    private inner class RotateListener : SimpleOnRotationGestureListener() {
        override fun onRotation(rotationDetector: RotationGestureDetector): Boolean {
            postRotate(rotationDetector.getAngle(), mMidPntX, mMidPntY)
            return true
        }
    }
}