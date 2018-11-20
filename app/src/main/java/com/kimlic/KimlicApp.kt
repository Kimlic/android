package com.kimlic

import android.app.Application
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.util.*

class KimlicApp : Application() {

    // Companion

    companion object {
        private const val MAX_ACTIVITY_TRANSITION_TIME_MS = 40000L // App background delay

        private var instance: KimlicApp? = null
        fun applicationContext(): Context = instance!!.applicationContext
    }

    // Variables

    private var activityTransitionTimer: Timer? = null
    private var activityTransitionTimeTask: TimerTask? = null

    @Volatile
    var wasInBackground = false
    @Volatile
    var isFirstTime: Boolean = true

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