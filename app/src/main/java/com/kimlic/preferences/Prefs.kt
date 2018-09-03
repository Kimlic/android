package com.kimlic.preferences

import com.kimlic.KimlicApp
import com.kimlic.utils.AppConstants

object Prefs : BasePreferences(KimlicApp.applicationContext()) {

    // Clear

    fun clear() {
        authenticated = false
        passcode = ""
        useFingerprint = false
        isPasscodeEnabled = false
        isTouchEnabled = false
        isRecoveryEnabled = false
        termsAccepted = false
        privacyAccepted = false
        isTutorialShown = false
        isPasscodeOffered = false
        isRecoveryOffered = false

        isDriveActive = false

        currentAccountAddress = ""
    }

    // Preferences

    var authenticated: Boolean
        get() = getBoolean(AppConstants.auth.key)
        set(value) = setBoolean(AppConstants.auth.key, value)

    var passcode: String
        get() = getString(AppConstants.passcode.key)
        set(value) = setString(AppConstants.passcode.key, value)

    var isPasscodeEnabled: Boolean
        get() = getBoolean(AppConstants.isPasscodeEnabled.key)
        set(value) = setBoolean(AppConstants.isPasscodeEnabled.key, value)

    var isPasscodeOffered: Boolean
        get() = getBoolean(AppConstants.isPasscodeOffered.key)
        set(value) = setBoolean(AppConstants.isPasscodeOffered.key, value)

    var isTouchEnabled: Boolean
        get() = getBoolean(AppConstants.isTouchEnabled.key)
        set(value) = setBoolean(AppConstants.isTouchEnabled.key, value)

    var isRecoveryEnabled: Boolean
        get() = getBoolean(AppConstants.isRecoveryEnabled.key)
        set(value) = setBoolean(AppConstants.isRecoveryEnabled.key, value)

    var isRecoveryOffered: Boolean
        get() = getBoolean(AppConstants.isRecoveryOffered.key)
        set(value) = setBoolean(AppConstants.isRecoveryOffered.key, value)

    var termsAccepted: Boolean
        get() = getBoolean(AppConstants.terms.key)
        set(value) = setBoolean(AppConstants.terms.key, value)

    var privacyAccepted: Boolean
        get() = getBoolean(AppConstants.privacy.key)
        set(value) = setBoolean(AppConstants.privacy.key, value)

    var isTutorialShown: Boolean
        get() = getBoolean(AppConstants.tutorials.key)
        set(value) = setBoolean(AppConstants.tutorials.key, value)

    var useFingerprint: Boolean
        get() = getBoolean(AppConstants.fingerprint.key)
        set(value) = setBoolean(AppConstants.fingerprint.key, value)

    var currentAccountAddress: String
        get() = getString(AppConstants.accountAddress.key)
        set(value) = setString(AppConstants.accountAddress.key, value)

    var isDriveActive: Boolean
        get() = getBoolean(AppConstants.isDriveActive.key)
        set(value) = setBoolean(AppConstants.isDriveActive.key, value)
}