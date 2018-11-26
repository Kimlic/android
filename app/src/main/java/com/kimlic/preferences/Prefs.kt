package com.kimlic.preferences

import com.kimlic.KimlicApp
import com.kimlic.utils.AppConstants

object Prefs : BasePreferences(KimlicApp.applicationContext()) {

    // Clear

    fun clear() {
        authenticated = false
        passcode = ""
        firebaseToken = ""
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
        currentMnemonic = ""
        newCompanyAccepted = false
        needCompanySyncCount = 0

        tryCount = 0
        currentLockInterval = 0
        timeToUnlock = 0
        risksTime = 0
    }

    // Preferences

    var authenticated: Boolean
        get() = getBoolean(AppConstants.AUTH.key)
        set(value) = setBoolean(AppConstants.AUTH.key, value)

    var passcode: String
        get() = getString(AppConstants.PASSCODE.key)
        set(value) = setString(AppConstants.PASSCODE.key, value)

    var isPasscodeEnabled: Boolean
        get() = getBoolean(AppConstants.IS_PASSCODE_ENABLED.key)
        set(value) = setBoolean(AppConstants.IS_PASSCODE_ENABLED.key, value)

    var isPasscodeOffered: Boolean
        get() = getBoolean(AppConstants.IS_PASSCODE_OFFERED.key)
        set(value) = setBoolean(AppConstants.IS_PASSCODE_OFFERED.key, value)

    var isTouchEnabled: Boolean
        get() = getBoolean(AppConstants.IS_TOUCH_ENABLED.key)
        set(value) = setBoolean(AppConstants.IS_TOUCH_ENABLED.key, value)

    var isRecoveryEnabled: Boolean
        get() = getBoolean(AppConstants.IS_RECOVERY_ENABLED.key)
        set(value) = setBoolean(AppConstants.IS_RECOVERY_ENABLED.key, value)

    var isRecoveryOffered: Boolean
        get() = getBoolean(AppConstants.IS_RECOVERY_OFFERED.key)
        set(value) = setBoolean(AppConstants.IS_RECOVERY_OFFERED.key, value)

    var termsAccepted: Boolean
        get() = getBoolean(AppConstants.TERMS.key)
        set(value) = setBoolean(AppConstants.TERMS.key, value)

    var privacyAccepted: Boolean
        get() = getBoolean(AppConstants.PRIVACY.key)
        set(value) = setBoolean(AppConstants.PRIVACY.key, value)

    var isTutorialShown: Boolean
        get() = getBoolean(AppConstants.TUTORIALS.key)
        set(value) = setBoolean(AppConstants.TUTORIALS.key, value)

    var useFingerprint: Boolean
        get() = getBoolean(AppConstants.FINGERPRINT.key)
        set(value) = setBoolean(AppConstants.FINGERPRINT.key, value)

    var currentAccountAddress: String
        get() = getString(AppConstants.ACCOUNT_ADDRESS.key)
        set(value) = setString(AppConstants.ACCOUNT_ADDRESS.key, value)

    var currentMnemonic: String
        get() = getString(AppConstants.MNEMONIC.key)
        set(value) = setString(AppConstants.MNEMONIC.key, value)

    var isDriveActive: Boolean
        get() = getBoolean(AppConstants.IS_DRIVE_ACTIVE.key)
        set(value) = setBoolean(AppConstants.IS_DRIVE_ACTIVE.key, value)

    var firebaseToken: String
        get() = getString(AppConstants.FIREBASE_TOKEN.key)
        set(value) = setString(AppConstants.FIREBASE_TOKEN.key, value)

    var newCompanyAccepted: Boolean
        get() = getBoolean(AppConstants.NEW_COMPANY_ACCEPTED.key)
        set(value) = setBoolean(AppConstants.NEW_COMPANY_ACCEPTED.key, value)

    var needCompanySyncCount: Int
        get() = getInt(AppConstants.NEED_COMPANY_SYNC.key)
        set(value) = setInt(AppConstants.NEED_COMPANY_SYNC.key, value)

    var needDocumentSyncCount: Int
        get() = getInt(AppConstants.NEED_DOCUMENT_SYNC.key)
        set(value) = setInt(AppConstants.NEED_DOCUMENT_SYNC.key, value)

    var currentLockInterval: Int
        get() = getInt(AppConstants.INTERVAL.key)
        set(value) = setInt(AppConstants.INTERVAL.key, value)

    var timeToUnlock: Long
        get() = getLong(AppConstants.UNLOCK_TIME.key)
        set(value) = setLong(AppConstants.UNLOCK_TIME.key, value)

    var tryCount: Int
        get() = getInt(AppConstants.TRY_COUNT.key)
        set(value) = setInt(AppConstants.TRY_COUNT.key, value)

    var risksTime: Long
        get() = getLong(AppConstants.RISKS_TIME.key)
        set(value) = setLong(AppConstants.RISKS_TIME.key, value)
}