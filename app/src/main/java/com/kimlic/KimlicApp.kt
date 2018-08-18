package com.kimlic

import android.app.Application
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class KimlicApp : Application() {

    // Companion

    companion object {
        private var instance: KimlicApp? = null

        fun applicationContext(): Context = instance!!.applicationContext
    }

    // Life

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this@KimlicApp, Crashlytics())
    }

    fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val intentFilter = IntentFilter()
        }
    }
}