package zzz.bing.himalaya.utils

import android.util.Log

object LogUtil {
    var isRelease = false
        set(value) {
            CURRENT_LEVEL = if (value){
                WARING_LEVEL
            }else{
                DEBUG_LEVEL
            }
            field = value
        }

    var CURRENT_LEVEL = 5
    set(value) {
        when {
            value >= DEBUG_LEVEL -> {
                field = value
            }
            value >= WARING_LEVEL -> {
                isRelease = true
                field = value
            }
            else -> {
                isRelease = true
                field = WARING_LEVEL
            }
        }
    }
    private const val DEBUG_LEVEL = 4
    private const val INFO_LEVEL = 3
    private const val WARING_LEVEL = 2
    private const val ERROR_LEVEL = 1

    fun d(clazz: Any, msg: String){
        if (CURRENT_LEVEL >= DEBUG_LEVEL){
            Log.d(clazz.javaClass.simpleName, "d: $msg")
        }

    }

    fun w(clazz: Any, msg: String){
        if (CURRENT_LEVEL >= WARING_LEVEL){
            Log.w(clazz.javaClass.simpleName, "w: $msg")
        }

    }

    fun e(clazz: Any, msg: String){
        if (CURRENT_LEVEL >= ERROR_LEVEL){
            Log.e(clazz.javaClass.simpleName, "e: $msg")
        }

    }

    fun i(clazz: Any, msg: String){
        if (CURRENT_LEVEL >= INFO_LEVEL){
            Log.i(clazz.javaClass.simpleName, "i: $msg")
        }

    }
}