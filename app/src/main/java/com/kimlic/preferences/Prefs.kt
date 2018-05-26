package com.kimlic.preferences

import android.util.Log
import com.kimlic.KimlicApp
import com.kimlic.utils.AppConstants

object Prefs : BasePreferences(KimlicApp.applicationContext()) {

    private val TAG = this.javaClass.simpleName

    init {
        Log.d(TAG, "initializing")
    }

    fun clear() {
        passcode = ""
        useFingerprint = false
    }

    var passcode: String
        get() = getString(AppConstants.passcode.key)
        set(value) = setString(AppConstants.passcode.key, value)

    var isTutorialShown: Boolean
        get() = getBoolean(AppConstants.tutorials.key)
        set(value) = setBoolean(AppConstants.tutorials.key, value)

    var useFingerprint: Boolean
        get() = getBoolean(AppConstants.fingerprint.key)
        set(value) = setBoolean(AppConstants.fingerprint.key, value)

}