package com.kimlic.utils

enum class AppConstants(val key: String, val intKey: Int = -1) {
    appPreferences("KIMLIC_PREFERENCES"),

    auth("AUTH"),

    passcode("PASSCODE"),

    isPasscodeEnabled("IS_PASSCODE_ENABLED"),
    isPasscodeOffered("IS_PASSCODE_OFFERED"),
    isTouchEnabled("IS_TOUCH_ENABLED"),
    isRecoveryEnabled("IS_RECOVERY_ENABLED"),
    isRecoveryOffered("IS_RECOVERY_OFFERED"),

    // UserPortraitPhoto
    isUserPhotoTaken("IS_USER_PHOTO_TAKEN"),

    terms("TERMS"),
    privacy("PRIVACY"),

    tutorials("TUTORIALS"),
    fingerprint("FINGERPRINT"),

    // Camer type

    cameraType("CAMERA_TYPE"),
    cameraFront("CAMERA_FRONT", 1),
    cameraRear("CAMERA_REAR", 0),

    // Settings type

    settingSwitch("SETTING_SWITCH", 1),
    settingIntent("SETTING_INFO", 2),

    // User photo files

    userPortraitFileName("portrait.jpg")
}
