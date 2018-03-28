package com.cokt.tool

import android.util.Log

object CoktLog {

    private val TAG = CoktLog::class.java.simpleName

    fun debug(log: String) {
        Log.d(TAG, log)
    }

    fun error(log: String) {
        Log.e(TAG, log)
    }

    fun warn(log: String) {
        Log.w(TAG, log)
    }

}