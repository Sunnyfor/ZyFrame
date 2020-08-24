package com.sunny.zy.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import com.sunny.zy.ZyFrameStore

/**
 * 网络请求判断
 */
class NetworkUtils {
    private val manager: ConnectivityManager by lazy {
        ZyFrameStore.getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun register() {
        manager.registerNetworkCallback(NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {

            })
    }
}