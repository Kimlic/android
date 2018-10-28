package com.kimlic

import android.app.Application
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.util.*

class KimlicApp : Application() {

    // Variables

    private var activityTransitionTimer: Timer? = null
    private var activityTransitionTimeTask: TimerTask? = null
    private val MAX_ACTIVITY_TRANSITION_TIME_MS = 5000L // App background delay
    @Volatile
    var wasInBackground = false
    @Volatile
    var isFirstTime: Boolean = true

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

    // Public

    fun startActivityTransitionTimer() {
        activityTransitionTimer = Timer()
        activityTransitionTimeTask = object : TimerTask() {
            override fun run() {
                wasInBackground = true
            }
        }
        activityTransitionTimer!!.schedule(activityTransitionTimeTask!!, MAX_ACTIVITY_TRANSITION_TIME_MS)
    }

    fun stopActivityTransitionTimer() {
        if (activityTransitionTimeTask != null) {
            activityTransitionTimer!!.cancel(); activityTransitionTimeTask = null
        }
        if (activityTransitionTimer != null) {
            activityTransitionTimer!!.cancel(); activityTransitionTimer = null
        }
        wasInBackground = false
    }
}