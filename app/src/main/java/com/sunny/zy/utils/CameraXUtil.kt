package com.sunny.zy.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.gallery.bean.GalleryBean
import com.sunny.zy.ZyFrameConfig
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/6/10 14:17
 */
class CameraXUtil {

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val imageCapture by lazy {
        ImageCapture.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
    }

    private val videoCapture by lazy {
        VideoCapture.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
    }

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var lifecycleOwner: LifecycleOwner

    private var screenAspectRatio = 0

    private var rotation = 0

    private lateinit var surfaceProvider: Preview.SurfaceProvider

    fun init(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        screenAspectRatio: Int,
        rotation: Int
    ) {
        this.lifecycleOwner = lifecycleOwner
        this.surfaceProvider = surfaceProvider
        this.screenAspectRatio = screenAspectRatio
        this.rotation = rotation
    }


    fun startQrCodeScan(
        selector: CameraSelector? = null,
        resultCallback: (text: String) -> Unit
    ) {
        val imageAnalyzer = ImageAnalysis.Builder().build().also {
            it.setAnalyzer(cameraExecutor, YUMImageAnalysis(resultCallback))
        }
        startCamera(selector, imageAnalyzer)
    }


    fun startCamera(
        selector: CameraSelector? = null,
        imageAnalyzer: ImageAnalysis? = null
    ) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(ZyFrameStore.getContext())
        cameraProviderFuture?.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider? = cameraProviderFuture?.get()

            if (selector != null) {
                cameraSelector = selector
            }

            if (cameraProvider?.hasCamera(cameraSelector) != true) {
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    ToastUtil.show("没有找到后置摄像头")
                }
                if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                    ToastUtil.show("没有找到前置摄像头")
                }
                return@Runnable
            }

            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera

                if (imageAnalyzer != null) {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } else {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        videoCapture
                    )
                }
                preview.setSurfaceProvider(surfaceProvider)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(ZyFrameStore.getContext()))
    }


    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }


    fun takePhoto(result: (galleryBean: GalleryBean) -> Unit) {
        val file = File(
            ZyFrameStore.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${StringUtil.getTimeStamp()}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(ZyFrameStore.getContext()),
            object :
                ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(
                            ZyFrameStore.getContext(),
                            ZyFrameConfig.authorities,
                            file
                        )
                    } else {
                        Uri.fromFile(file)
                    }

                    val galleryBean = GalleryBean(0, uri)
                    galleryBean.size = file.length()
                    galleryBean.type = "image/jpg"
                    result.invoke(galleryBean)
                }

                override fun onError(exception: ImageCaptureException) {
                    file.delete()
                    exception.printStackTrace()
                }

            })
    }


    @SuppressLint("RestrictedApi")
    fun stopVideo() {
        videoCapture.stopRecording()
    }


    @SuppressLint("RestrictedApi", "MissingPermission")
    fun takeVideo(result: (galleryBean: GalleryBean) -> Unit) {
        val file = File(
            ZyFrameStore.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "${StringUtil.getTimeStamp()}.mp4"
        )

        val outputOptions = VideoCapture.OutputFileOptions.Builder(file).build()

        videoCapture.startRecording(
            outputOptions, ContextCompat.getMainExecutor(ZyFrameStore.getContext()),
            object : VideoCapture.OnVideoSavedCallback {

                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    //保存视频成功回调，会在停止录制时被调用
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(
                            ZyFrameStore.getContext(),
                            ZyFrameConfig.authorities,
                            file
                        )
                    } else {
                        Uri.fromFile(file)
                    }
                    val galleryBean = GalleryBean(0, uri)
                    galleryBean.size = file.length()
                    galleryBean.type = "video/mp4"
                    result.invoke(galleryBean)
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    //保存视频失败
                    LogUtil.i("视频录制失败！")
                }

            })
    }


    private inner class YUMImageAnalysis(var resultCallback: (text: String) -> Unit) :
        ImageAnalysis.Analyzer {

        private val reader: MultiFormatReader = MultiFormatReader()

        override fun analyze(image: ImageProxy) {
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
                val result = reader.decode(bitmap).text
                onDestroy()
                Handler(Looper.getMainLooper()).post {
                    resultCallback.invoke(result)
                }

            } catch (e: NotFoundException) {
                image.close()
            } finally {
                image.close()
            }
        }
    }


    fun onDestroy() {
        cameraExecutor.shutdown()
    }

    fun deleteMove(uri: Uri) {
        ZyFrameStore.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.let {
            deleteFile(it, uri)
        }
    }

    fun deletePicture(uri: Uri) {
        ZyFrameStore.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
            deleteFile(it, uri)
        }
    }


    private fun deleteFile(parentFile: File, uri: Uri) {
        File(parentFile, uri.toString().split("/").last()).delete()
    }


    companion object {

        const val RATIO_4_3_VALUE = 4.0 / 3.0
        const val RATIO_16_9_VALUE = 16.0 / 9.0

        fun createQRCode(content: String, qrcode_size: Int = 300): Bitmap? {
            val hashMap = HashMap<EncodeHintType, Any>()
            // 设置二维码字符编码
            hashMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
            // 设置二维码纠错等级
            hashMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
            // 设置二维码边距
            hashMap[EncodeHintType.MARGIN] = 2

            try {
                // 开始生成二维码
                val bitMatrix = MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE,
                    qrcode_size, qrcode_size, hashMap
                )

                val pixels = IntArray(qrcode_size * qrcode_size)
                for (y in 0 until qrcode_size) {
                    for (x in 0 until qrcode_size) {
                        //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                        if (bitMatrix[x, y]) {
                            pixels[y * qrcode_size + x] = Color.BLACK //黑色色块像素设置
                        } else {
                            pixels[y * qrcode_size + x] = Color.WHITE // 白色色块像素设置
                        }
                    }
                }
                /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
                val bitmap = Bitmap.createBitmap(qrcode_size, qrcode_size, Bitmap.Config.ARGB_8888)
                bitmap.setPixels(pixels, 0, qrcode_size, 0, 0, qrcode_size, qrcode_size)
                return bitmap
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            return null
        }

        fun aspectRatio(width: Int, height: Int): Int {
            val previewRatio = max(width, height).toDouble() / min(width, height)
            if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
                return AspectRatio.RATIO_4_3
            }
            return AspectRatio.RATIO_16_9
        }
    }

}