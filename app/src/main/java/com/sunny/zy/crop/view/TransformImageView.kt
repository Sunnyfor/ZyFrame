package com.sunny.zy.crop.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sunny.kit.utils.LogUtil
import com.sunny.zy.crop.util.RectUtils.getCenterFromRect
import com.sunny.zy.crop.util.RectUtils.getCornersFromRect
import com.sunny.zy.glide.GlideApp
import java.io.File
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 *
 *
 * This class provides base logic to setup the image, transform it with matrix (move, scale, rotate),
 * and methods to get current matrix state.
 */
open class TransformImageView : AppCompatImageView {


    @JvmField
    protected val mCurrentImageCorners = FloatArray(RECT_CORNER_POINTS_COORDS)

    @JvmField
    protected val mCurrentImageCenter = FloatArray(RECT_CENTER_POINT_COORDS)
    private val mMatrixValues = FloatArray(MATRIX_VALUES_COUNT)


    protected var mCurrentImageMatrix = Matrix()
    protected var mThisWidth = 0
    protected var mThisHeight = 0
    protected var mTransformImageListener: TransformImageListener? = null
    private var mInitialImageCorners = floatArrayOf()
    private var mInitialImageCenter = floatArrayOf()
    private var mBitmapDecoded = false
    protected var mBitmapLaidOut = false

    var imageFile: File? = null

    var initWidth = 0
    var initHeight = 0

    var viewBitmap: Bitmap? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        scaleType = ScaleType.MATRIX
    }

    /**
     * Interface for rotation and scale change notifying.
     */
    interface TransformImageListener {
        fun onLoadComplete()
        fun onLoadFailure(e: Exception)
        fun onRotate(currentAngle: Float)
        fun onScale(currentScale: Float)
    }


    fun setTransformImageListener(transformImageListener: TransformImageListener?) {
        mTransformImageListener = transformImageListener
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType)
        } else {
            LogUtil.w(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used")
        }
    }


    /**
     * This method takes an Uri as a parameter, then calls method to decode it into Bitmap with specified size.
     *
     */
    fun initImageFile(imageFile: File) {
        this.imageFile = imageFile
        GlideApp.with(context)
            .asBitmap()
            .load(imageFile)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    mBitmapDecoded = true
                    initWidth = resource.width
                    initHeight = resource.height
                    viewBitmap = resource
                    setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    /**
     * @return - current image scale value.
     * [1.0f - for original image, 2.0f - for 200% scaled image, etc.]
     */
    var currentScale: Float = 0f
        get() = getMatrixScale(mCurrentImageMatrix)

    /**
     * This method calculates scale value for given Matrix object.
     */
    private fun getMatrixScale(matrix: Matrix): Float {
        return sqrt(
            getMatrixValue(matrix, Matrix.MSCALE_X).toDouble().pow(2.0)
                    + getMatrixValue(matrix, Matrix.MSKEW_Y).toDouble().pow(2.0)
        ).toFloat()
    }

    /**
     * @return - current image rotation angle.
     */
    val currentAngle: Float
        get() = getMatrixAngle(mCurrentImageMatrix)

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    private fun getMatrixAngle(matrix: Matrix): Float {
        return -(atan2(
            getMatrixValue(matrix, Matrix.MSKEW_X).toDouble(),
            getMatrixValue(matrix, Matrix.MSCALE_X).toDouble()
        ) * (180 / Math.PI)).toFloat()
    }

    override fun setImageMatrix(matrix: Matrix) {
        super.setImageMatrix(matrix)
        mCurrentImageMatrix.set(matrix)
        updateCurrentImagePoints()
    }


    /**
     * This method translates current image.
     *
     * @param deltaX - horizontal shift
     * @param deltaY - vertical shift
     */
    fun postTranslate(deltaX: Float, deltaY: Float) {
        if (deltaX != 0f || deltaY != 0f) {
            mCurrentImageMatrix.postTranslate(deltaX, deltaY)
            imageMatrix = mCurrentImageMatrix
        }
    }

    /**
     * This method scales current image.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    open fun postScale(deltaScale: Float, px: Float, py: Float) {
        if (deltaScale != 0f) {
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py)
            imageMatrix = mCurrentImageMatrix
            mTransformImageListener?.onScale(getMatrixScale(mCurrentImageMatrix))

        }
    }

    /**
     * This method rotates current image.
     *
     * @param deltaAngle - rotation angle
     * @param px         - rotation center X
     * @param py         - rotation center Y
     */
    fun postRotate(deltaAngle: Float, px: Float, py: Float) {
        if (deltaAngle != 0f) {
            mCurrentImageMatrix.postRotate(deltaAngle, px, py)
            imageMatrix = mCurrentImageMatrix
            mTransformImageListener?.onRotate(getMatrixAngle(mCurrentImageMatrix))

        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var mLeft = left
        var mTop = top
        var mRight = right
        var mBottom = bottom
        super.onLayout(changed, mLeft, mTop, mRight, mBottom)
        if (changed || mBitmapDecoded && !mBitmapLaidOut) {
            mLeft = paddingLeft
            mTop = paddingTop
            mRight = width - paddingRight
            mBottom = height - paddingBottom
            mThisWidth = mRight - mLeft
            mThisHeight = mBottom - mTop
            onImageLaidOut()
        }
    }

    /**
     * When image is laid out [.mInitialImageCenter] and [.mInitialImageCenter]
     * must be set.
     */
    protected open fun onImageLaidOut() {
        val drawable = drawable ?: return
        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        LogUtil.i(TAG, String.format("Image size: [%d:%d]", w.toInt(), h.toInt()))
        val initialImageRect = RectF(0f, 0f, w, h)
        mInitialImageCorners = getCornersFromRect(initialImageRect)
        mInitialImageCenter = getCenterFromRect(initialImageRect)
        mBitmapLaidOut = true
        if (mTransformImageListener != null) {
            mTransformImageListener!!.onLoadComplete()
        }
    }

    /**
     * This method returns Matrix value for given index.
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See [Matrix.MSCALE_X] and others.
     * @return - matrix value for index
     */
    private fun getMatrixValue(
        matrix: Matrix,
        @IntRange(
            from = 0,
            to = MATRIX_VALUES_COUNT.toLong()
        ) valueIndex: Int
    ): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[valueIndex]
    }

    /**
     * This method logs given matrix X, Y, scale, and angle values.
     * Can be used for debug.
     */
    protected fun printMatrix(logPrefix: String, matrix: Matrix) {
        val x = getMatrixValue(matrix, Matrix.MTRANS_X)
        val y = getMatrixValue(matrix, Matrix.MTRANS_Y)
        val rScale = getMatrixScale(matrix)
        val rAngle = getMatrixAngle(matrix)
        LogUtil.i(TAG, "$logPrefix: matrix: { x: $x, y: $y, scale: $rScale, angle: $rAngle }")
    }

    /**
     * This method updates current image corners and center points that are stored in
     * [.mCurrentImageCorners] and [.mCurrentImageCenter] arrays.
     * Those are used for several calculations.
     */
    private fun updateCurrentImagePoints() {
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners)
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter)
    }

    companion object {
        private const val TAG = "TransformImageView"
        private const val RECT_CORNER_POINTS_COORDS = 8
        private const val RECT_CENTER_POINT_COORDS = 2
        private const val MATRIX_VALUES_COUNT = 9
    }
}