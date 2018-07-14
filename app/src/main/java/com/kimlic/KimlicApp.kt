package com.kimlic

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.kimlic.quorum.QuorumKimlic
import java.util.*
import android.provider.SyncStateContract.Helpers.update
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.crashlytics.android.Crashlytics
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.quorum.DeviceID
import com.kimlic.quorum.Sha
import com.kimlic.utils.QuorumURL
import io.fabric.sdk.android.Fabric
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


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

    }

    fun registerReceiver(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val intentFilter = IntentFilter()
        }

    }
}