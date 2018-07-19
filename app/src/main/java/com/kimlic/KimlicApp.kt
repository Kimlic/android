package com.kimlic

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Build
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.kimlic.db.KimlicDB
import com.kimlic.db.User
import com.kimlic.preferences.Prefs
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

//        KimlicDB.getInstance()!!.userDao().findById(Prefs.userId).observe(this, object : Observer<User> {
//            override fun onChanged(t: User?) {
//                if (t != null) {
//                    Log.d(TAG, t.toString())
//                }
//            }
//        })

    }

    fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val intentFilter = IntentFilter()
        }

    }
}