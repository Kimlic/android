package com.kimlic.preferences

import com.kimlic.KimlicApp
import com.kimlic.utils.AppConstants

object Prefs : BasePreferences(KimlicApp.applicationContext()) {

    private val TAG = this.javaClass.simpleName

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
        isUserPhotoTaken = false

        // User profile preferences

        auth = ""
        userName = ""
        userLastName = ""
        userPhone = ""
        isUserPhoneAccepted = false
        userEmail = ""
        userAddress = ""
        documentToverify = ""
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
        get() = getBoolean(AppConstants.isTouchEnabled.key)
        set(value) = setBoolean(AppConstants.isTouchEnabled.key, value)

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

    // User photo

    var isUserPhotoTaken: Boolean
        get() = getBoolean(AppConstants.isUserPhotoTaken.key)
        set(value) = setBoolean(AppConstants.isUserPhotoTaken.key, value)

    // Profile preferences

    var auth: String
        get() = getString(AppConstants.userAuth.key)
        set(value) = setString(AppConstants.userAuth.key, value)

    val userId: Long = 0L

    var userName: String
        get() = getString(AppConstants.userName.key)
        set(value) = setString(AppConstants.userName.key, value)

    var userLastName: String
        get() = getString(AppConstants.userLastName.key)
        set(value) = setString(AppConstants.userLastName.key, value)

    var userPhone: String
        get() = getString(AppConstants.userPhone.key)
        set(value) = setString(AppConstants.userPhone.key, value)

    var isUserPhoneAccepted: Boolean
        get() = getBoolean(AppConstants.userPhoneAccepted.key)
        set(value) = setBoolean(AppConstants.userPhoneAccepted.key, value)

    var userEmail: String
        get() = getString(AppConstants.userEmail.key)
        set(value) = setString(AppConstants.userEmail.key, value)

    var isUserEmailAccepted: Boolean
        get() = getBoolean(AppConstants.userEmailAccepted.key)
        set(value) = setBoolean(AppConstants.userEmailAccepted.key, value)

    var userAddress: String
        get() = getString(AppConstants.userAddress.key)
        set(value) = setString(AppConstants.userAddress.key, value)

    var documentToverify: String
        get() = getString(AppConstants.documentToVerify.key)
        set(value) = setString(AppConstants.documentToVerify.key, value)

}