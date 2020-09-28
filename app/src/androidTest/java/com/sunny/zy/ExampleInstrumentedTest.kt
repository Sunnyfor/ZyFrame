package com.sunny.zy

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.http.ZyHttp
import com.sunny.zy.http.bean.DownLoadResultBean
import com.sunny.zy.http.bean.HttpResultBean
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Before
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sunny.zy.test", appContext.packageName)
        ZyFrameStore.init(appContext)
    }

    @Test
    fun login() {
        runBlocking {
            launch(Main) {
                val httpResultBean = object : HttpResultBean<Any>() {}
                val url = "http://10.0.0.58/app/appcarinoroutapply/list?proposer=&page=1"
                val params = JSONObject()
                params.put("username", "123456")
                params.put("password", "123456")
                ZyHttp.postJson(url, params.toString(), httpResultBean)
                Log.i("登录请求", httpResultBean.toString())
            }
        }
    }

    @Test
    fun downLoad() {
        runBlocking {
            launch(Main) {
                val url =
                    "https://c-ssl.duitang.com/uploads/item/201703/31/20170331090953_zUcaS.thumb.700_0.jpeg"
                val downLoadResultBean = object : DownLoadResultBean() {
                    override fun notifyData(downLoadResultBean: DownLoadResultBean) {

                        Log.i("开始下载", "进度:${downLoadResultBean.readLength}")

                        if (downLoadResultBean.done && downLoadResultBean.file != null) {
                            Log.i("下载完成", "进度:${downLoadResultBean.file}")
                        }
                    }
                }
                ZyHttp.get(url, null, downLoadResultBean)
            }
        }
    }
}