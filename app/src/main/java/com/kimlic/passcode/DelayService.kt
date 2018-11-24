package com.kimlic.passcode

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DelayService : Service() {

    private var count: Int = 1

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d("TAGSERVICE", "onStart")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("TAGSERVICE", "onBind")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAGSERVICE", "onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAGSERVICE", "onStartCommand startId = $startId")
        Log.d("TAGSERVICE", "onStartCommand countValue = ${count++}")


        return START_STICKY
    }
}