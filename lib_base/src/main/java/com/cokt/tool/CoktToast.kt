package com.cokt.tool

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object CoktToast {
    private var context: Context? = null

    fun init(ctx: Context) {
        context = ctx
    }

    fun uninit() {
        context = null
    }

    fun toast(content: String) {
        if (context != null && !TextUtils.isEmpty(content)) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
        }
    }

    fun toast(resID: Int) {
        if (context != null) {
            Toast.makeText(context, resID, Toast.LENGTH_SHORT).show()
        }
    }

}