package com.sunny.zy.crop.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.sunny.zy.R
import com.sunny.zy.crop.callback.OverlayViewChangeListener
import com.sunny.zy.crop.util.RectUtils.getCenterFromRect
import com.sunny.zy.crop.util.RectUtils.getCornersFromRect
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 *
 *
 * This view is used for drawing the overlay on top of the image. It may have frame, crop guidelines and dimmed area.
 * This must have LAYER_TYPE_SOFTWARE to draw itself properly.
 */
class OverlayView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val cropViewRect = RectF()
    private val mTempRect = RectF()
    private var mThisWidth = 0
    private var mThisHeight = 0
    private var mCropGridCorners = floatArrayOf()
    private var mCropGridCenter = floatArrayOf()
    private var mCropGridRowCount = 0
    private var mCropGridColumnCount = 0
    private var mTargetAspectRatio = 0f
    private var mGridPoints: FloatArray? = null
    private var mShowCropFrame = false
    private var mShowCropGrid = false
    private var mCircleDimmedLayer = false
    private var mDimmedColor = 0
    private val mCircularPath = Path()
    private val mDimmedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropFrameCornersPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mFreestyleCropMode = false
    private var mPreviousTouchX = -1f
    private var mPreviousTouchY = -1f
    private var mCurrentTouchCornerIndex = -1
    private var mTouchPointThreshold = 0
    private var mCropRectMinSize = 0
    private var mCropRectCornerTouchAreaLineLength = 0
    var overlayViewChangeListener: OverlayViewChangeListener? = null
    private var mShouldSetupCropBounds = false

    init {
        mTouchPointThreshold = resources.getDimensionPixelSize(R.dimen.dp_30)
        mCropRectMinSize = resources.getDimensionPixelSize(R.dimen.dp_100)
        mCropRectCornerTouchAreaLineLength = resources.getDimensionPixelSize(R.dimen.dp_10)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * Setter for [.mCircleDimmedLayer] variable.
     *
     * @param circleDimmedLayer - set it to true if you want dimmed layer to be an circle
     */
    fun setCircleDimmedLayer(circleDimmedLayer: Boolean) {
        mCircleDimmedLayer = circleDimmedLayer
    }

    /**
     * Setter for crop grid rows count.
     * Resets [.mGridPoints] variable because it is not valid anymore.
     */
    fun setCropGridRowCount(@IntRange(from = 0) cropGridRowCount: Int) {
        mCropGridRowCount = cropGridRowCount
        mGridPoints = null
    }

    /**
     * Setter for crop grid columns count.
     * Resets [.mGridPoints] variable because it is not valid anymore.
     */
    fun setCropGridColumnCount(@IntRange(from = 0) cropGridColumnCount: Int) {
        mCropGridColumnCount = cropGridColumnCount
        mGridPoints = null
    }

    /**
     * Setter for [.mShowCropFrame] variable.
     *
     * @param showCropFrame - set to true if you want to see a crop frame rectangle on top of an image
     */
    fun setShowCropFrame(showCropFrame: Boolean) {
        mShowCropFrame = showCropFrame
    }

    /**
     * Setter for [.mShowCropGrid] variable.
     *
     * @param showCropGrid - set to true if you want to see a crop grid on top of an image
     */
    fun setShowCropGrid(showCropGrid: Boolean) {
        mShowCropGrid = showCropGrid
    }

    /**
     * Setter for [.mDimmedColor] variable.
     *
     * @param dimmedColor - desired color of dimmed area around the crop bounds
     */
    fun setDimmedColor(@ColorInt dimmedColor: Int) {
        mDimmedColor = dimmedColor
    }

    /**
     * Setter for crop frame stroke width
     */
    fun setCropFrameStrokeWidth(@IntRange(from = 0) width: Int) {
        mCropFramePaint.strokeWidth = width.toFloat()
    }

    /**
     * Setter for crop grid stroke width
     */
    fun setCropGridStrokeWidth(@IntRange(from = 0) width: Int) {
        mCropGridPaint.strokeWidth = width.toFloat()
    }


    /**
     * Setter for crop frame color
     */
    fun setCropFrameColor(@ColorInt color: Int) {
        mCropFramePaint.color = color
    }

    /**
     * Setter for crop grid color
     */
    fun setCropGridColor(@ColorInt color: Int) {
        mCropGridPaint.color = color
    }

    /**
     * Setter for crop grid corner color
     */
    fun setCropGridCornerColor(@ColorInt color: Int) {
        mCropFrameCornersPaint.color = color
    }

    /**
     * This method sets aspect ratio for crop bounds.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    fun setTargetAspectRatio(targetAspectRatio: Float) {
        mTargetAspectRatio = targetAspectRatio
        if (mThisWidth > 0) {
            setupCropBounds()
            postInvalidate()
        } else {
            mShouldSetupCropBounds = true
        }
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * [.mCropViewRect] is used to draw crop bounds - uses padding.
     */
    private fun setupCropBounds() {
        val height = (mThisWidth / mTargetAspectRatio).toInt()
        if (height > mThisHeight) {
            val width = (mThisHeight * mTargetAspectRatio).toInt()
            val halfDiff = (mThisWidth - width) / 2
            cropViewRect[(paddingLeft + halfDiff).toFloat(), paddingTop.toFloat(), (
                    paddingLeft + width + halfDiff).toFloat()] =
                (paddingTop + mThisHeight).toFloat()
        } else {
            val halfDiff = (mThisHeight - height) / 2
            cropViewRect[paddingLeft.toFloat(), (paddingTop + halfDiff).toFloat(), (
                    paddingLeft + mThisWidth).toFloat()] =
                (paddingTop + height + halfDiff).toFloat()
        }

        overlayViewChangeListener?.onCropRectUpdated(cropViewRect)

        updateGridPoints()
    }

    private fun updateGridPoints() {
        mCropGridCorners = getCornersFromRect(cropViewRect)
        mCropGridCenter = getCenterFromRect(cropViewRect)
        mGridPoints = null
        mCircularPath.reset()
        mCircularPath.addCircle(
            cropViewRect.centerX(), cropViewRect.centerY(),
            cropViewRect.width().coerceAtMost(cropViewRect.height()) / 2f, Path.Direction.CW
        )
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var mLeft = left
        var mTop = top
        var mRight = right
        var mBottom = bottom
        super.onLayout(changed, mLeft, mTop, mRight, mBottom)
        if (changed) {
            mLeft = paddingLeft
            mTop = paddingTop
            mRight = width - paddingRight
            mBottom = height - paddingBottom
            mThisWidth = mRight - mLeft
            mThisHeight = mBottom - mTop
            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false
                setTargetAspectRatio(mTargetAspectRatio)
            }
        }
    }

    /**
     * Along with image there are dimmed layer, crop bounds and crop guidelines that must be drawn.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDimmedLayer(canvas)
        drawCropGrid(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (cropViewRect.isEmpty || !mFreestyleCropMode) {
            return false
        }
        var x = event.x
        var y = event.y
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
            mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y)
            val shouldHandle = mCurrentTouchCornerIndex != -1
            if (!shouldHandle) {
                mPreviousTouchX = -1f
                mPreviousTouchY = -1f
            } else if (mPreviousTouchX < 0) {
                mPreviousTouchX = x
                mPreviousTouchY = y
            }
            return shouldHandle
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE) {
            if (event.pointerCount == 1 && mCurrentTouchCornerIndex != -1) {
                x = x.coerceAtLeast(paddingLeft.toFloat())
                    .coerceAtMost((width - paddingRight).toFloat())
                y = y.coerceAtLeast(paddingTop.toFloat())
                    .coerceAtMost((height - paddingBottom).toFloat())
                updateCropViewRect(x, y)
                mPreviousTouchX = x
                mPreviousTouchY = y
                return true
            }
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            mPreviousTouchX = -1f
            mPreviousTouchY = -1f
            mCurrentTouchCornerIndex = -1
            overlayViewChangeListener?.onCropRectUpdated(cropViewRect)
        }
        return false
    }

    /**
     * * The order of the corners is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     */
    private fun updateCropViewRect(touchX: Float, touchY: Float) {
        mTempRect.set(cropViewRect)
        when (mCurrentTouchCornerIndex) {
            0 -> mTempRect[touchX, touchY, cropViewRect.right] = cropViewRect.bottom
            1 -> mTempRect[cropViewRect.left, touchY, touchX] = cropViewRect.bottom
            2 -> mTempRect[cropViewRect.left, cropViewRect.top, touchX] = touchY
            3 -> mTempRect[touchX, cropViewRect.top, cropViewRect.right] = touchY
            4 -> {
                mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY)
                if (mTempRect.left > left && mTempRect.top > top && mTempRect.right < right && mTempRect.bottom < bottom) {
                    cropViewRect.set(mTempRect)
                    updateGridPoints()
                    postInvalidate()
                }
                return
            }
        }
        val changeHeight = mTempRect.height() >= mCropRectMinSize
        val changeWidth = mTempRect.width() >= mCropRectMinSize
        cropViewRect[if (changeWidth) mTempRect.left else cropViewRect.left, if (changeHeight) mTempRect.top else cropViewRect.top, if (changeWidth) mTempRect.right else cropViewRect.right] =
            if (changeHeight) mTempRect.bottom else cropViewRect.bottom
        if (changeHeight || changeWidth) {
            updateGridPoints()
            postInvalidate()
        }
    }

    /**
     * * The order of the corners in the float array is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     *
     * @return - index of corner that is being dragged
     */
    private fun getCurrentTouchIndex(touchX: Float, touchY: Float): Int {
        var closestPointIndex = -1
        var closestPointDistance = mTouchPointThreshold.toDouble()
        var i = 0
        while (i < 8) {
            val distanceToCorner = sqrt(
                (touchX - mCropGridCorners[i]).toDouble().pow(2.0)
                        + (touchY - mCropGridCorners[i + 1]).toDouble().pow(2.0)
            )
            if (distanceToCorner < closestPointDistance) {
                closestPointDistance = distanceToCorner
                closestPointIndex = i / 2
            }
            i += 2
        }
        return if (mFreestyleCropMode && closestPointIndex < 0 && cropViewRect.contains(
                touchX,
                touchY
            )
        ) {
            4
        } else closestPointIndex
    }

    /**
     * This method draws dimmed area around the crop bounds.
     *
     * @param canvas - valid canvas object
     */
    private fun drawDimmedLayer(canvas: Canvas) {
        canvas.save()
        if (mCircleDimmedLayer) {
            canvas.clipPath(mCircularPath, Region.Op.DIFFERENCE)
        } else {
            canvas.clipRect(cropViewRect, Region.Op.DIFFERENCE)
        }
        canvas.drawColor(mDimmedColor)
        canvas.restore()
        if (mCircleDimmedLayer) { // Draw 1px stroke to fix antialias
            canvas.drawCircle(
                cropViewRect.centerX(), cropViewRect.centerY(),
                cropViewRect.width().coerceAtMost(cropViewRect.height()) / 2f, mDimmedStrokePaint
            )
        }
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    private fun drawCropGrid(canvas: Canvas) {
        if (mShowCropGrid) {
            if (mGridPoints == null && !cropViewRect.isEmpty) {
                mGridPoints = FloatArray(mCropGridRowCount * 4 + mCropGridColumnCount * 4)
                var index = 0
                for (i in 0 until mCropGridRowCount) {
                    mGridPoints!![index++] = cropViewRect.left
                    mGridPoints!![index++] =
                        cropViewRect.height() * ((i.toFloat() + 1.0f) / (mCropGridRowCount + 1).toFloat()) + cropViewRect.top
                    mGridPoints!![index++] = cropViewRect.right
                    mGridPoints!![index++] =
                        cropViewRect.height() * ((i.toFloat() + 1.0f) / (mCropGridRowCount + 1).toFloat()) + cropViewRect.top
                }
                for (i in 0 until mCropGridColumnCount) {
                    mGridPoints!![index++] =
                        cropViewRect.width() * ((i.toFloat() + 1.0f) / (mCropGridColumnCount + 1).toFloat()) + cropViewRect.left
                    mGridPoints!![index++] = cropViewRect.top
                    mGridPoints!![index++] =
                        cropViewRect.width() * ((i.toFloat() + 1.0f) / (mCropGridColumnCount + 1).toFloat()) + cropViewRect.left
                    mGridPoints!![index++] = cropViewRect.bottom
                }
            }
            if (mGridPoints != null) {
                canvas.drawLines(mGridPoints!!, mCropGridPaint)
            }
        }
        if (mShowCropFrame) {
            canvas.drawRect(cropViewRect, mCropFramePaint)
        }
        if (mFreestyleCropMode) {
            canvas.save()
            mTempRect.set(cropViewRect)
            mTempRect.inset(
                mCropRectCornerTouchAreaLineLength.toFloat(),
                -mCropRectCornerTouchAreaLineLength.toFloat()
            )
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE)
            mTempRect.set(cropViewRect)
            mTempRect.inset(
                -mCropRectCornerTouchAreaLineLength.toFloat(),
                mCropRectCornerTouchAreaLineLength.toFloat()
            )
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE)
            canvas.drawRect(cropViewRect, mCropFrameCornersPaint)
            canvas.restore()
        }
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    fun processStyledAttributes() {
        mDimmedStrokePaint.color = mDimmedColor
        mDimmedStrokePaint.style = Paint.Style.STROKE
        mDimmedStrokePaint.strokeWidth = 1f
        initCropFrameStyle()
        initCropGridStyle()
    }

    /**
     * This method setups Paint object for the crop bounds.
     */
    private fun initCropFrameStyle() {
        mCropFramePaint.style = Paint.Style.STROKE
        mCropFrameCornersPaint.strokeWidth = (mCropFramePaint.strokeWidth * 3)
        mCropFrameCornersPaint.style = Paint.Style.STROKE
    }

    /**
     * This method setups Paint object for the crop guidelines.
     */
    private fun initCropGridStyle() {
        val cropGridStrokeSize = resources.getDimensionPixelSize(R.dimen.dp_1)
        mCropGridPaint.strokeWidth = cropGridStrokeSize.toFloat()
    }

}