package com.cokt

import android.app.Application
import com.cokt.tool.CoktToast

class CoktApplication : Application() {

    override fun onCreate() {
        CoktToast.init(applicationContext)
        super.onCreate()
    }

}