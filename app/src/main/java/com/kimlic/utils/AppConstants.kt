package com.kimlic.utils

enum class AppConstants(val key: String, val intKey: Int = -1) {

    appPreferences("KIMLIC_PREFERENCES"),

    auth("AUTH"),
    userId("USERID"),
    accountAddress("ACCOUNT_ADDRESS"),

    passcode("PASSCODE"),

    isPasscodeEnabled("IS_PASSCODE_ENABLED"),
    isPasscodeOffered("IS_PASSCODE_OFFERED"),
    isTouchEnabled("IS_TOUCH_ENABLED"),
    isRecoveryEnabled("IS_RECOVERY_ENABLED"),
    isRecoveryOffered("IS_RECOVERY_OFFERED"),

    isGoogleSigneIn("IS_GOOGLE_SIGNE_IN"),
    isDriveActive("IS_GOOGLE_DRIVE_ACTIVE"),

    terms("TERMS"),
    privacy("PRIVACY"),

    tutorials("TUTORIALS"),
    fingerprint("FINGERPRINT"),

    // Camera

    cameraType("CAMERA_TYPE"),
    cameraFront("CAMERA_FRONT", 1),
    cameraRear("CAMERA_REAR", 0),

    // Document type

    documentType("DOCUMENT_TYPE"),
    documentToVerify("DOCUMENT_TO_VERIFY"),
    documentByteArray("DOCUMENT_BYTE_ARRAY"),

    // Photo type

    photoFaceType("face"),
    photoFrontType("document-front"),
    photoBackType("document-back"),
    photoAddressType("address"),

    portraitDocument("portrait_document"),


    COUNTRY("country"),

    // File path

    filePathRezult("FILE_PATH_REZULT"),

    // Settings type

    settingSwitch("SETTING_SWITCH", 1),
    settingIntent("SETTING_INFO", 2),

    // User profile preferences

    userAuth("USER_AUTH"),

    // Help constants

    errorDescription("ERROR")
}