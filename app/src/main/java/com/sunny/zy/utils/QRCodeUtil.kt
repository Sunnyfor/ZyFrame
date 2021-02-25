package com.sunny.zy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.SurfaceView
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.widget.AutoFitSurfaceView

/**
 * Desc
 * Author JoannChen
 * Mail yongzuo.chen@foxmail.com
 * Date 2020/10/25 22:51
 */
class QRCodeUtil(
    private val resultCallback: (result: String) -> Unit
) {

    var result = ""

    var cameraReadyCallBack: (() -> Unit)? = null

    var cameraFacing = CameraCharacteristics.LENS_FACING_BACK

    private var imageReader: ImageReader? = null

    private var mSession: CameraCaptureSession? = null

    private var camera: CameraDevice? = null

    private val cameraThread: HandlerThread by lazy {
        HandlerThread("CameraThread").apply { start() }
    }
    private val cameraHandler: Handler by lazy {
        Handler(cameraThread.looper)
    }


    private val imageReaderThread: HandlerThread by lazy {
        HandlerThread("imageReaderThread").apply { start() }
    }

    private val imageReaderHandler: Handler by lazy {
        Handler(imageReaderThread.looper)
    }


    private val hintsMap = mapOf<DecodeHintType, Collection<BarcodeFormat>>(
        Pair(DecodeHintType.POSSIBLE_FORMATS, arrayListOf(BarcodeFormat.QR_CODE))
    )
    private val reader: MultiFormatReader = MultiFormatReader()

    private var isReady = false

    private var cameraId = ""

    var characteristics: CameraCharacteristics? = null

    /**
     * 此方法需要获取摄像头权限后再调用
     */
    @SuppressLint("MissingPermission")
    fun open(surface: AutoFitSurfaceView) {
        imageReader =
            ImageReader.newInstance(surface.height, surface.width, ImageFormat.YUV_420_888, 3)
        imageReader?.setOnImageAvailableListener({
            if (result.isNotEmpty()) {
                return@setOnImageAvailableListener
            }

            val image = it.acquireLatestImage() ?: return@setOnImageAvailableListener

            if (it.imageFormat != ImageFormat.YUV_420_888) {
                image.close()
                return@setOnImageAvailableListener
            }

            val yBuffer = image.planes[0].buffer // Y
            val uBuffer = image.planes[1].buffer // U
            val vBuffer = image.planes[2].buffer // V

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val data = ByteArray(ySize + uSize + vSize)
            yBuffer.get(data, 0, ySize)
            vBuffer.get(data, ySize, vSize)
            uBuffer.get(data, ySize + vSize, uSize)

            val source =
                PlanarYUVLuminanceSource(
                    data, image.width, image.height, 0, 0, image.width, image.height, false
                )
            val bitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                result = reader.decode(bitmap, hintsMap).text
                resultCallback.invoke(result)
            } catch (e: NotFoundException) {
                image.close()
            }
        }, imageReaderHandler)

        getCameraManager().openCamera(
            cameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    this@QRCodeUtil.camera = camera
                    createCaptureSession(surface)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    this@QRCodeUtil.camera = camera

                }

                override fun onError(camera: CameraDevice, error: Int) {
                    this@QRCodeUtil.camera = camera
                }

            },
            cameraHandler
        )

    }

    fun queryCameraId(): String {
        val cameraList = queryCamera()
        cameraList.forEach {
            val characteristics = getCameraManager().getCameraCharacteristics(it)
            if (cameraFacing == characteristics.get(CameraCharacteristics.LENS_FACING)) {
                cameraId = it
                this.characteristics = characteristics
                return cameraId
            }
        }
        return ""
    }

    private fun createCaptureSession(surface: SurfaceView) {
        val targets = listOf(surface.holder.surface, imageReader?.surface)
        camera?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${camera?.id} session configuration failed")
                LogUtil.i(exc.message ?: "")
            }

            override fun onConfigured(session: CameraCaptureSession) {
                mSession = session
                createCaptureRequest(surface.holder.surface)
            }
        }, cameraHandler)
    }

    private fun createCaptureRequest(surface: Surface) {
        val captureRequest = camera?.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        )
        captureRequest?.addTarget(surface)
        captureRequest?.addTarget(imageReader?.surface ?: return)
        // 设置连续自动对焦和自动曝光
        captureRequest?.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        );
        captureRequest?.set(
            CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        );
        mSession?.setRepeatingRequest(captureRequest?.build() ?: return, object :
            CameraCaptureSession.CaptureCallback() {
            override fun onCaptureStarted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                timestamp: Long,
                frameNumber: Long
            ) {
                super.onCaptureStarted(session, request, timestamp, frameNumber)
                if (!isReady) {
                    isReady = true
                    cameraReadyCallBack?.invoke()
                }
            }
        }, cameraHandler)

    }

    fun queryCamera(): Array<String> {
        return getCameraManager().cameraIdList
    }


    fun getCameraManager(): CameraManager {
        return ZyFrameStore.getContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }


    fun onClose() {
        camera?.close()
        camera = null
        mSession?.close()
        imageReader?.close()
        imageReader?.setOnImageAvailableListener(null, null)
    }
}