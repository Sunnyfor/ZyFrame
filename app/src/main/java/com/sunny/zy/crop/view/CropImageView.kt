package com.sunny.zy.crop.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.IntRange
import com.sunny.kit.utils.LogUtil
import com.sunny.zy.crop.callback.CropBoundsChangeListener
import com.sunny.zy.crop.util.CubicEasing
import com.sunny.zy.crop.util.RectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 *
 *
 * This class adds crop feature, methods to draw crop guidelines, and keep image in correct state.
 * Also it extends parent class methods to add checks for scale; animating zoom in/out.
 */
open class CropImageView : TransformImageView {
    private val mCropRect = RectF()
    private val mTempMatrix = Matrix()
    private var mTargetAspectRatio = 0f
    private var mMaxScaleMultiplier = 10.0f
    var cropBoundsChangeListener: CropBoundsChangeListener? = null
    private var mWrapCropBoundsRunnable: Runnable? = null
    private var mZoomImageToPositionRunnable: Runnable? = null

    /**
     * @return - maximum scale value for current image and crop ratio
     */
    var maxScale = 0f
        private set

    /**
     * @return - minimum scale value for current image and crop ratio
     */
    var minScale = 0f
        private set
    private var mMaxResultImageSizeX = 0
    private var mMaxResultImageSizeY = 0
    private var mImageToWrapCropBoundsAnimDuration = 500L

    private var cropOffsetX = 0f
    private var cropOffsetY = 0f
    private var mCroppedImageWidth = 0f
    private var mCroppedImageHeight = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun crop(): Boolean {
        val mCurrentImageRect = RectUtils.trapToRect(mCurrentImageCorners)
        if (mCurrentImageRect.isEmpty) {
            LogUtil.i("CurrentImageRect is empty")
            return false
        }
        if (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0) {
            val cropWidth: Float = mCropRect.width() / currentScale
            val cropHeight: Float = mCropRect.height() / currentScale

            if (cropWidth > mMaxResultImageSizeX || cropHeight > mMaxResultImageSizeY) {
                val scaleX = mMaxResultImageSizeX / cropWidth
                val scaleY = mMaxResultImageSizeY / cropHeight
                val resizeScale = scaleX.coerceAtMost(scaleY)
                viewBitmap?.let {
                    val resizedBitmap = Bitmap.createScaledBitmap(
                        it,
                        (it.width * resizeScale).roundToInt(),
                        (it.height * resizeScale).roundToInt(), false
                    )
                    if (it != resizedBitmap) {
                        it.recycle()
                    }
                    viewBitmap = resizedBitmap
                    currentScale /= resizeScale
                }

            }
        }

        // Rotate if needed
        if (currentAngle != 0f) {
            viewBitmap?.let {
                val tempMatrix = Matrix()
                tempMatrix.setRotate(
                    currentAngle,
                    (it.width / 2).toFloat(),
                    (it.height / 2).toFloat()
                )
                val rotatedBitmap = Bitmap.createBitmap(
                    it, 0, 0, it.width, it.height,
                    tempMatrix, true
                )
                viewBitmap = rotatedBitmap
            }

        }

        cropOffsetX =
            ((mCropRect.left - mCurrentImageRect.left) / currentScale).roundToInt().toFloat()
        cropOffsetY =
            ((mCropRect.top - mCurrentImageRect.top) / currentScale).roundToInt().toFloat()
        mCroppedImageWidth = (mCropRect.width() / currentScale).roundToInt().toFloat()
        mCroppedImageHeight = (mCropRect.height() / currentScale).roundToInt().toFloat()

        var pixelError = 1
        pixelError += (mCroppedImageWidth.coerceAtLeast(mCroppedImageHeight) / 1000f).roundToInt()
        val shouldCrop =
            (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0
                    || abs(mCropRect.left - mCurrentImageRect.left) > pixelError
                    || abs(mCropRect.top - mCurrentImageRect.top) > pixelError
                    || abs(mCropRect.bottom - mCurrentImageRect.bottom) > pixelError
                    || abs(mCropRect.right - mCurrentImageRect.right) > pixelError
                    || currentAngle != 0f)

        if (shouldCrop) {
            if (cropOffsetX < 0) {
                cropOffsetX = 0f
                mCroppedImageWidth = viewBitmap?.width?.toFloat() ?: 0f
            }
            if (cropOffsetY < 0) {
                cropOffsetY = 0f
                mCroppedImageHeight = viewBitmap?.height?.toFloat() ?: 0f
            }
            viewBitmap?.let {
                val croppedBitmap = Bitmap.createBitmap(
                    it,
                    cropOffsetX.toInt(),
                    cropOffsetY.toInt(),
                    mCroppedImageWidth.toInt(),
                    mCroppedImageHeight.toInt()
                )
                val outStream = ByteArrayOutputStream()
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                imageFile?.writeBytes(outStream.toByteArray())
                croppedBitmap.recycle()
                outStream.close()
            }
        }
        return true
    }


    /**
     * Cancels all current animations and sets image to fill crop area (without animation).
     * Then creates and executes [] with proper parameters.
     */
    suspend fun saveImage():File? {
        cancelAllAnimations()
        setImageToWrapCropBounds(false)
      return withContext(Dispatchers.IO) {
            crop()
            imageFile
        }
    }
    /**
     * @return - aspect ratio for crop bounds
     */
    /**
     * This method sets aspect ratio for crop bounds.
     * If [.SOURCE_IMAGE_ASPECT_RATIO] value is passed - aspect ratio is calculated
     * based on current image width and height.
     * targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    var targetAspectRatio: Float
        get() = mTargetAspectRatio
        set(targetAspectRatio) {
            val drawable = drawable
            if (drawable == null) {
                mTargetAspectRatio = targetAspectRatio
                return
            }
            mTargetAspectRatio = if (targetAspectRatio == 0F) {
                drawable.intrinsicWidth / drawable.intrinsicHeight.toFloat()
            } else {
                targetAspectRatio
            }
            cropBoundsChangeListener?.onCropAspectRatioChanged(mTargetAspectRatio)

        }

    /**
     * Updates current crop rectangle with given. Also recalculates image properties and position
     * to fit new crop rectangle.
     *
     * @param cropRect - new crop rectangle
     */
    fun setCropRect(cropRect: RectF) {
        mTargetAspectRatio = cropRect.width() / cropRect.height()
        mCropRect[cropRect.left - paddingLeft, cropRect.top - paddingTop, cropRect.right - paddingRight] =
            cropRect.bottom - paddingBottom
        calculateImageScaleBounds()
        setImageToWrapCropBounds()
    }

    /**
     * This method sets maximum width for resulting cropped image
     *
     * @param maxResultImageSizeX - size in pixels
     */
    fun setMaxResultImageSizeX(@IntRange(from = 10) maxResultImageSizeX: Int) {
        mMaxResultImageSizeX = maxResultImageSizeX
    }

    /**
     * This method sets maximum width for resulting cropped image
     *
     * @param maxResultImageSizeY - size in pixels
     */
    fun setMaxResultImageSizeY(@IntRange(from = 10) maxResultImageSizeY: Int) {
        mMaxResultImageSizeY = maxResultImageSizeY
    }

    /**
     * This method sets animation duration for image to wrap the crop bounds
     *
     * @param imageToWrapCropBoundsAnimDuration - duration in milliseconds
     */
    fun setImageToWrapCropBoundsAnimDuration(@IntRange(from = 100) imageToWrapCropBoundsAnimDuration: Long) {
        mImageToWrapCropBoundsAnimDuration = if (imageToWrapCropBoundsAnimDuration > 0) {
            imageToWrapCropBoundsAnimDuration
        } else {
            throw IllegalArgumentException("Animation duration cannot be negative value.")
        }
    }

    /**
     * This method sets multiplier that is used to calculate max image scale from min image scale.
     *
     * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
     */
    fun setMaxScaleMultiplier(maxScaleMultiplier: Float) {
        mMaxScaleMultiplier = maxScaleMultiplier
    }

    /**
     * This method scales image down for given value related to image center.
     */
    fun zoomOutImage(deltaScale: Float) {
        zoomOutImage(deltaScale, mCropRect.centerX(), mCropRect.centerY())
    }

    /**
     * This method scales image down for given value related given coords (x, y).
     */
    fun zoomOutImage(scale: Float, centerX: Float, centerY: Float) {
        if (scale >= minScale) {
            postScale(scale / currentScale, centerX, centerY)
        }
    }

    /**
     * This method scales image up for given value related to image center.
     */
    fun zoomInImage(deltaScale: Float) {
        zoomInImage(deltaScale, mCropRect.centerX(), mCropRect.centerY())
    }

    /**
     * This method scales image up for given value related to given coords (x, y).
     */
    fun zoomInImage(scale: Float, centerX: Float, centerY: Float) {
        if (scale <= maxScale) {
            postScale(scale / currentScale, centerX, centerY)
        }
    }

    /**
     * This method changes image scale for given value related to point (px, py) but only if
     * resulting scale is in min/max bounds.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    override fun postScale(deltaScale: Float, px: Float, py: Float) {
        if (deltaScale > 1 && currentScale * deltaScale <= maxScale) {
            super.postScale(deltaScale, px, py)
        } else if (deltaScale < 1 && currentScale * deltaScale >= minScale) {
            super.postScale(deltaScale, px, py)
        }
    }

    /**
     * This method rotates image for given angle related to the image center.
     *
     * @param deltaAngle - angle to rotate
     */
    fun postRotate(deltaAngle: Float) {
        postRotate(deltaAngle, mCropRect.centerX(), mCropRect.centerY())
    }

    /**
     * This method cancels all current Runnable objects that represent animations.
     */
    fun cancelAllAnimations() {
        removeCallbacks(mWrapCropBoundsRunnable)
        removeCallbacks(mZoomImageToPositionRunnable)
    }

    fun setImageToWrapCropBounds() {
        setImageToWrapCropBounds(true)
    }

    /**
     * If image doesn't fill the crop bounds it must be translated and scaled properly to fill those.
     *
     *
     * Therefore this method calculates delta X, Y and scale values and passes them to the
     * [WrapCropBoundsRunnable] which animates image.
     * Scale value must be calculated only if image won't fill the crop bounds after it's translated to the
     * crop bounds rectangle center. Using temporary variables this method checks this case.
     */
    private fun setImageToWrapCropBounds(animate: Boolean) {
        if (mBitmapLaidOut && !isImageWrapCropBounds) {
            val currentX = mCurrentImageCenter[0]
            val currentY = mCurrentImageCenter[1]
            val currentScale = currentScale
            var deltaX = mCropRect.centerX() - currentX
            var deltaY = mCropRect.centerY() - currentY
            var deltaScale = 0f
            mTempMatrix.reset()
            mTempMatrix.setTranslate(deltaX, deltaY)
            val tempCurrentImageCorners =
                mCurrentImageCorners.copyOf(mCurrentImageCorners.size)
            mTempMatrix.mapPoints(tempCurrentImageCorners)
            val willImageWrapCropBoundsAfterTranslate =
                isImageWrapCropBounds(tempCurrentImageCorners)
            if (willImageWrapCropBoundsAfterTranslate) {
                val imageIndents = calculateImageIndents()
                deltaX = -(imageIndents[0] + imageIndents[2])
                deltaY = -(imageIndents[1] + imageIndents[3])
            } else {
                val tempCropRect = RectF(mCropRect)
                mTempMatrix.reset()
                mTempMatrix.setRotate(currentAngle)
                mTempMatrix.mapRect(tempCropRect)
                val currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners)
                deltaScale =
                    (tempCropRect.width() / currentImageSides[0]).coerceAtLeast(tempCropRect.height() / currentImageSides[1])
                deltaScale = deltaScale * currentScale - currentScale
            }
            if (animate) {
                post(WrapCropBoundsRunnable(
                    this@CropImageView,
                    mImageToWrapCropBoundsAnimDuration,
                    currentX,
                    currentY,
                    deltaX,
                    deltaY,
                    currentScale,
                    deltaScale,
                    willImageWrapCropBoundsAfterTranslate
                ).also { mWrapCropBoundsRunnable = it })
            } else {
                postTranslate(deltaX, deltaY)
                if (!willImageWrapCropBoundsAfterTranslate) {
                    zoomInImage(currentScale + deltaScale, mCropRect.centerX(), mCropRect.centerY())
                }
            }
        }
    }

    /**
     * First, un-rotate image and crop rectangles (make image rectangle axis-aligned).
     * Second, calculate deltas between those rectangles sides.
     * Third, depending on delta (its sign) put them or zero inside an array.
     * Fourth, using Matrix, rotate back those points (indents).
     *
     * @return - the float array of image indents (4 floats) - in this order [left, top, right, bottom]
     */
    private fun calculateImageIndents(): FloatArray {
        mTempMatrix.reset()
        mTempMatrix.setRotate(-currentAngle)
        val unRotatedImageCorners = mCurrentImageCorners.copyOf(mCurrentImageCorners.size)
        val unRotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect)
        mTempMatrix.mapPoints(unRotatedImageCorners)
        mTempMatrix.mapPoints(unRotatedCropBoundsCorners)
        val unRotatedImageRect = RectUtils.trapToRect(unRotatedImageCorners)
        val unRotatedCropRect = RectUtils.trapToRect(unRotatedCropBoundsCorners)
        val deltaLeft = unRotatedImageRect.left - unRotatedCropRect.left
        val deltaTop = unRotatedImageRect.top - unRotatedCropRect.top
        val deltaRight = unRotatedImageRect.right - unRotatedCropRect.right
        val deltaBottom = unRotatedImageRect.bottom - unRotatedCropRect.bottom
        val indents = FloatArray(4)
        indents[0] = if (deltaLeft > 0) deltaLeft else 0F
        indents[1] = if (deltaTop > 0) deltaTop else 0F
        indents[2] = if (deltaRight < 0) deltaRight else 0F
        indents[3] = if (deltaBottom < 0) deltaBottom else 0F
        mTempMatrix.reset()
        mTempMatrix.setRotate(currentAngle)
        mTempMatrix.mapPoints(indents)
        return indents
    }

    /**
     * When image is laid out it must be centered properly to fit current crop bounds.
     */
    override fun onImageLaidOut() {
        super.onImageLaidOut()
        val drawable = drawable ?: return
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()
        if (mTargetAspectRatio == 0F) {
            mTargetAspectRatio = drawableWidth / drawableHeight
        }
        val height = (mThisWidth / mTargetAspectRatio).toInt()
        if (height > mThisHeight) {
            val width = (mThisHeight * mTargetAspectRatio).toInt()
            val halfDiff = (mThisWidth - width) / 2
            mCropRect[halfDiff.toFloat(), 0f, (width + halfDiff).toFloat()] = mThisHeight.toFloat()
        } else {
            val halfDiff = (mThisHeight - height) / 2
            mCropRect[0f, halfDiff.toFloat(), mThisWidth.toFloat()] = (height + halfDiff).toFloat()
        }
        calculateImageScaleBounds(drawableWidth, drawableHeight)
        setupInitialImagePosition(drawableWidth, drawableHeight)

        cropBoundsChangeListener?.onCropAspectRatioChanged(mTargetAspectRatio)
        mTransformImageListener?.onScale(currentScale)
        mTransformImageListener?.onRotate(currentAngle)

    }

    /**
     * This method checks whether current image fills the crop bounds.
     */
    protected val isImageWrapCropBounds: Boolean
        get() = isImageWrapCropBounds(mCurrentImageCorners)

    /**
     * This methods checks whether a rectangle that is represented as 4 corner points (8 floats)
     * fills the crop bounds rectangle.
     *
     * @param imageCorners - corners of a rectangle
     * @return - true if it wraps crop bounds, false - otherwise
     */
    private fun isImageWrapCropBounds(imageCorners: FloatArray): Boolean {
        mTempMatrix.reset()
        mTempMatrix.setRotate(-currentAngle)
        val unRotatedImageCorners = imageCorners.copyOf(imageCorners.size)
        mTempMatrix.mapPoints(unRotatedImageCorners)
        val unRotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect)
        mTempMatrix.mapPoints(unRotatedCropBoundsCorners)
        return RectUtils.trapToRect(unRotatedImageCorners)
            .contains(RectUtils.trapToRect(unRotatedCropBoundsCorners))
    }

    /**
     * This method changes image scale (animating zoom for given duration), related to given center (x,y).
     *
     * @param scale      - target scale
     * @param centerX    - scale center X
     * @param centerY    - scale center Y
     * @param durationMs - zoom animation duration
     */
    protected fun zoomImageToPosition(
        scale: Float,
        centerX: Float,
        centerY: Float
    ) {
        var mScale = scale
        if (mScale > maxScale) {
            mScale = maxScale
        }
        val oldScale = currentScale
        val deltaScale = mScale - oldScale
        post(ZoomImageToPosition(
            this@CropImageView,
            200, oldScale, deltaScale, centerX, centerY
        ).also { mZoomImageToPositionRunnable = it })
    }

    private fun calculateImageScaleBounds() {
        val drawable = drawable ?: return
        calculateImageScaleBounds(
            drawable.intrinsicWidth.toFloat(),
            drawable.intrinsicHeight.toFloat()
        )
    }

    /**
     * This method calculates image minimum and maximum scale values for current [.mCropRect].
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private fun calculateImageScaleBounds(drawableWidth: Float, drawableHeight: Float) {
        val widthScale =
            (mCropRect.width() / drawableWidth).coerceAtMost(mCropRect.width() / drawableHeight)
        val heightScale =
            (mCropRect.height() / drawableHeight).coerceAtMost(mCropRect.height() / drawableWidth)
        minScale = widthScale.coerceAtMost(heightScale)
        maxScale = minScale * mMaxScaleMultiplier
    }

    /**
     * This method calculates initial image position so it is positioned properly.
     * Then it sets those values to the current image matrix.
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private fun setupInitialImagePosition(drawableWidth: Float, drawableHeight: Float) {
        val cropRectWidth = mCropRect.width()
        val cropRectHeight = mCropRect.height()
        val widthScale = mCropRect.width() / drawableWidth
        val heightScale = mCropRect.height() / drawableHeight
        val initialMinScale = widthScale.coerceAtLeast(heightScale)
        val tw = (cropRectWidth - drawableWidth * initialMinScale) / 2.0f + mCropRect.left
        val th = (cropRectHeight - drawableHeight * initialMinScale) / 2.0f + mCropRect.top
        mCurrentImageMatrix.reset()
        mCurrentImageMatrix.postScale(initialMinScale, initialMinScale)
        mCurrentImageMatrix.postTranslate(tw, th)
        imageMatrix = mCurrentImageMatrix
    }


    /**
     * This Runnable is used to animate an image so it fills the crop bounds entirely.
     * Given values are interpolated during the animation time.
     * Runnable can be terminated either vie [.cancelAllAnimations] method
     * or when certain conditions inside [WrapCropBoundsRunnable.run] method are triggered.
     */
    private class WrapCropBoundsRunnable(
        cropImageView: CropImageView,
        durationMs: Long,
        oldX: Float, oldY: Float,
        centerDiffX: Float, centerDiffY: Float,
        oldScale: Float, deltaScale: Float,
        willBeImageInBoundsAfterTranslate: Boolean
    ) : Runnable {
        private val mCropImageView: WeakReference<CropImageView>
        private val mDurationMs: Long
        private val mStartTime: Long
        private val mOldX: Float
        private val mOldY: Float
        private val mCenterDiffX: Float
        private val mCenterDiffY: Float
        private val mOldScale: Float
        private val mDeltaScale: Float
        private val mWillBeImageInBoundsAfterTranslate: Boolean

        init {
            mCropImageView = WeakReference(cropImageView)
            mDurationMs = durationMs
            mStartTime = System.currentTimeMillis()
            mOldX = oldX
            mOldY = oldY
            mCenterDiffX = centerDiffX
            mCenterDiffY = centerDiffY
            mOldScale = oldScale
            mDeltaScale = deltaScale
            mWillBeImageInBoundsAfterTranslate = willBeImageInBoundsAfterTranslate
        }

        override fun run() {
            val cropImageView = mCropImageView.get() ?: return
            val now = System.currentTimeMillis()
            val currentMs = mDurationMs.coerceAtMost(now - mStartTime).toFloat()
            val newX = CubicEasing.easeOut(currentMs, 0f, mCenterDiffX, mDurationMs.toFloat())
            val newY = CubicEasing.easeOut(currentMs, 0f, mCenterDiffY, mDurationMs.toFloat())
            val newScale = CubicEasing.easeInOut(currentMs, 0f, mDeltaScale, mDurationMs.toFloat())
            if (currentMs < mDurationMs) {
                cropImageView.postTranslate(
                    newX - (cropImageView.mCurrentImageCenter[0] - mOldX),
                    newY - (cropImageView.mCurrentImageCenter[1] - mOldY)
                )
                if (!mWillBeImageInBoundsAfterTranslate) {
                    cropImageView.zoomInImage(
                        mOldScale + newScale,
                        cropImageView.mCropRect.centerX(),
                        cropImageView.mCropRect.centerY()
                    )
                }
                if (!cropImageView.isImageWrapCropBounds) {
                    cropImageView.post(this)
                }
            }
        }
    }

    /**
     * This Runnable is used to animate an image zoom.
     * Given values are interpolated during the animation time.
     * Runnable can be terminated either vie [.cancelAllAnimations] method
     * or when certain conditions inside [ZoomImageToPosition.run] method are triggered.
     */
    private class ZoomImageToPosition(
        cropImageView: CropImageView,
        durationMs: Long,
        oldScale: Float, deltaScale: Float,
        destX: Float, destY: Float
    ) : Runnable {
        private val mCropImageView: WeakReference<CropImageView>
        private val mDurationMs: Long
        private val mStartTime: Long
        private val mOldScale: Float
        private val mDeltaScale: Float
        private val mDestX: Float
        private val mDestY: Float

        init {
            mCropImageView = WeakReference(cropImageView)
            mStartTime = System.currentTimeMillis()
            mDurationMs = durationMs
            mOldScale = oldScale
            mDeltaScale = deltaScale
            mDestX = destX
            mDestY = destY
        }

        override fun run() {
            val cropImageView = mCropImageView.get() ?: return
            val now = System.currentTimeMillis()
            val currentMs = mDurationMs.coerceAtMost(now - mStartTime).toFloat()
            val newScale = CubicEasing.easeInOut(currentMs, 0f, mDeltaScale, mDurationMs.toFloat())
            if (currentMs < mDurationMs) {
                cropImageView.zoomInImage(mOldScale + newScale, mDestX, mDestY)
                cropImageView.post(this)
            } else {
                cropImageView.setImageToWrapCropBounds()
            }
        }
    }
}