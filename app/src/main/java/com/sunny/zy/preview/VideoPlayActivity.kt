package com.sunny.zy.preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sunny.zy.R
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.ToastUtil
import kotlinx.android.synthetic.main.zy_act_preview_video.*

/**
 * Desc
 * Author ZY
 * Mail zhangye98@foxmail.com
 * Date 2021/9/29 16:41
 */
class VideoPlayActivity : BaseActivity() {

    companion object {
        fun intent(
            context: AppCompatActivity,
            uri: Uri,
            resultCallback: ((resultCode: Int, uri: Uri) -> Unit)? = null
        ) {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("uri", uri)
            if (resultCallback != null) {
                intent.putExtra("isComplete", true)
                context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    resultCallback(it.resultCode, uri)
                }.launch(intent)
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

    private val isComplete by lazy {
        intent.getBooleanExtra("isComplete", false)
    }

    private val mediaController by lazy {
        MediaController(this)
    }

    override fun initLayout() = R.layout.zy_act_preview_video

    override fun initView() {
        setStatusBarColor(R.color.preview_bg)

        if (isComplete) {
            tv_complete.visibility = View.VISIBLE
        }

        setOnClickListener(zy_ib_back, tv_complete)
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
            zy_ib_back.id -> {
                finish()
            }
            tv_complete.id -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onClose() {
        videoView.setMediaController(null)
        videoView.stopPlayback()
    }
}