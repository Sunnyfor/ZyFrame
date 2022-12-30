package com.sunny.zy.preview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.sunny.kit.utils.ToastUtil
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.IntentManager

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/29 16:41
 */
class VideoPlayActivity : BaseActivity() {

    companion object {

        fun intent(
            context: AppCompatActivity,
            launcher: ActivityResultLauncher<Intent>? = null,
            uri: Uri
        ) {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("uri", uri)
            if (launcher != null) {
                intent.putExtra("isShowComplete", true)
                launcher.launch(intent)
            } else {
                context.startActivity(intent)
            }
        }

        fun intent(context: Context, url: String) {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }


    private val uri by lazy {
        intent.getParcelableExtra<Uri>("uri")
    }

    private val url by lazy {
        intent.getStringExtra("url")
    }

    private val isShowComplete by lazy {
        intent.getBooleanExtra("isShowComplete", false)
    }

    private val mediaController by lazy {
        MediaController(this)
    }

    private var isComplete = false

    private val tvComplete by lazy {
        findViewById<TextView>(R.id.tvComplete)
    }

    private val btnBack by lazy {
        findViewById<ImageButton>(R.id.btnBack)
    }

    private val videoView by lazy {
        findViewById<VideoView>(R.id.videoView)
    }

    override fun initLayout() = R.layout.zy_act_preview_video

    override fun initView() {
//        setStatusBarColor(R.color.preview_bg)

        if (isShowComplete) {
            tvComplete.visibility = View.VISIBLE
        }

        setOnClickListener(btnBack, tvComplete)
    }

    override fun loadData() {
        if (uri != null) {
            videoView.setVideoURI(uri)
        }

        if (url != null) {
            videoView.setVideoPath(url)
        }


        videoView.setOnPreparedListener {
//            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            it.start()
        }

        videoView.setOnErrorListener { _, _, _ ->
            finish()
            ToastUtil.show("无法播放此视频")
            return@setOnErrorListener true
        }

        videoView.setMediaController(mediaController)
    }

    override fun onClickEvent(view: View) {
        when (view.id) {
            btnBack.id -> {
                isComplete = false
                finish()
            }
            tvComplete.id -> {
                isComplete = true
                finish()
            }
        }
    }

    override fun onClose() {
        videoView.setMediaController(null)
        videoView.stopPlayback()
        IntentManager.videoPlayResultCallBack?.invoke(isComplete)
        IntentManager.videoPlayResultCallBack = null
    }
}