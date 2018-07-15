package com.kimlic

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.crashlytics.android.Crashlytics
import com.kimlic.db.KimlicDB
import io.fabric.sdk.android.Fabric


class KimlicApp : Application() {

    // Constants

    private val TAG = this.javaClass.simpleName

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
        KimlicDB.getInstance(applicationContext)
    }

    fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentFilter = IntentFilter()
        }
    }


}