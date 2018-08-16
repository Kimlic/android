package com.kimlic

import android.app.Application
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import android.annotation.SuppressLint
import java.security.SecureRandom
import javax.net.ssl.*
import javax.security.cert.X509Certificate


class KimlicApp : Application() {

  // Constants

  private val TAG = this.javaClass.simpleName

  // Companion

  companion object {
    private var instance: KimlicApp? = null

    fun applicationContext(): Context =  instance!!.applicationContext
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