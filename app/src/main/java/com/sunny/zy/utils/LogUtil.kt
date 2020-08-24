@file:Suppress("MemberVisibilityCanPrivate")

package com.sunny.zy.utils

import android.text.TextUtils
import android.util.Log
import com.sunny.zy.http.ZyConfig

/**
 * 移植xUtils日志代码
 * Created by 张野 on 2017/10/12.
 */

object LogUtil {
    private var customTagPrefix = ""
    private var allowD = ZyConfig.isLog
    private var allowE = ZyConfig.isLog
    private var allowI = ZyConfig.isLog
    private var allowV = ZyConfig.isLog
    private var allowW = ZyConfig.isLog
    private var allowWtf = ZyConfig.isLog
    private var customLogger: CustomLogger? = null


    fun d(content: String) {
        if (allowD) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.d(tag, content)
            } else {
                Log.d(tag, content)
            }

        }
    }

    fun d(content: String, tr: Throwable) {
        if (allowD) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.d(tag, content, tr)
            } else {
                Log.d(tag, content, tr)
            }

        }
    }

    fun e(content: String) {
        if (allowE) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.e(tag, content)
            } else {
                Log.e(tag, content)
            }

        }
    }

    fun e(content: String, tr: Throwable) {
        if (allowE) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.e(tag, content, tr)
            } else {
                Log.e(tag, content, tr)
            }

        }
    }

    fun i(content: String) {
        if (allowI) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.i(tag, content)
            } else {
                Log.i(tag, content)
            }

        }
    }

    fun i(content: String, tr: Throwable) {
        if (allowI) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.i(tag, content, tr)
            } else {
                Log.i(tag, content, tr)
            }

        }
    }

    fun v(content: String) {
        if (allowV) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.v(tag, content)
            } else {
                Log.v(tag, content)
            }

        }
    }

    fun v(content: String, tr: Throwable) {
        if (allowV) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.v(tag, content, tr)
            } else {
                Log.v(tag, content, tr)
            }

        }
    }

    fun w(content: String) {
        if (allowW) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.w(tag, content)
            } else {
                Log.w(tag, content)
            }

        }
    }

    fun w(content: String, tr: Throwable) {
        if (allowW) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.w(tag, content, tr)
            } else {
                Log.w(tag, content, tr)
            }

        }
    }

    fun w(tr: Throwable) {
        if (allowW) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.w(tag, tr)
            } else {
                Log.w(tag, tr)
            }

        }
    }

    fun wtf(content: String) {
        if (allowWtf) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.wtf(tag, content)
            } else {
                Log.wtf(tag, content)
            }

        }
    }

    fun wtf(content: String, tr: Throwable) {
        if (allowWtf) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.wtf(tag, content, tr)
            } else {
                Log.wtf(tag, content, tr)
            }

        }
    }

    fun wtf(tr: Throwable) {
        if (allowWtf) {
            val caller = getCallerStackTraceElement()
            val tag = generateTag(caller)
            if (customLogger != null) {
                customLogger?.wtf(tag, tr)
            } else {
                Log.wtf(tag, tr)
            }

        }
    }


    private fun generateTag(caller: StackTraceElement): String {
        var tag = "%s.%s(L:%d)"
        var callerClazzName = caller.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        tag = String.format(tag, callerClazzName, caller.methodName, Integer.valueOf(caller.lineNumber))
        tag = if (TextUtils.isEmpty(customTagPrefix)) tag else customTagPrefix + ":" + tag
        return tag
    }

    private fun getCallerStackTraceElement(): StackTraceElement =
            Thread.currentThread().stackTrace[4]

    interface CustomLogger {
        fun d(var1: String, var2: String)

        fun d(var1: String, var2: String, var3: Throwable)

        fun e(var1: String, var2: String)

        fun e(var1: String, var2: String, var3: Throwable)

        fun i(var1: String, var2: String)

        fun i(var1: String, var2: String, var3: Throwable)

        fun v(var1: String, var2: String)

        fun v(var1: String, var2: String, var3: Throwable)

        fun w(var1: String, var2: String)

        fun w(var1: String, var2: String, var3: Throwable)

        fun w(var1: String, var2: Throwable)

        fun wtf(var1: String, var2: String)

        fun wtf(var1: String, var2: String, var3: Throwable)

        fun wtf(var1: String, var2: Throwable)
    }
}
