package com.sunny.zy.http.bean

import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/4/13 11:28
 */
abstract class WebSocketResultBean : WebSocketListener() {
    var webSocket: WebSocket? = null
}