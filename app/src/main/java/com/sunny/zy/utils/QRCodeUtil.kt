package com.sunny.zy.utils

import android.os.Handler
import android.os.Looper
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.sunny.zy.ZyFrameStore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/6/10 14:17
 */
class QRCodeUtil(private var resultCallback: (text: String) -> Unit) {

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    fun init(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        selector: CameraSelector? = null
    ) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(ZyFrameStore.getContext())
        cameraProviderFuture?.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider? = cameraProviderFuture?.get()

            val cameraSelector = selector ?: CameraSelector.DEFAULT_BACK_CAMERA

            if (cameraProvider?.hasCamera(cameraSelector) != true) {
                ToastUtil.show("没有匹配到摄像头")
                return@Runnable
            }
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(surfaceProvider)
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()


                val imageAnalyzer = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(cameraExecutor, YUMImageAnalysis())
                }
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                LogUtil.i("Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(ZyFrameStore.getContext()))
    }


    fun onDestroy() {
        cameraExecutor.shutdown()
    }


    private inner class YUMImageAnalysis : ImageAnalysis.Analyzer {

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
}