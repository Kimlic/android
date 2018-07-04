package com.kimlic

import android.app.Application
import android.content.Context
import com.android.volley.RequestQueue

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
}