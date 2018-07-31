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
    documentPassport("passport"),
    documentLicense("license"),
    documentID("id"),
    documentPermit("permit"),
    documentToVerify("DOCUMENT_TO_VERIFY"),
    documentByteArray("DOCUMENT_BYTE_ARRAY"),

    // File path

    filePathRezult("FILE_PATH_REZULT"),

    // UserPortraitPhoto

    isUserPhotoTaken("IS_USER_PHOTO_TAKEN"),

    // Settings type

    settingSwitch("SETTING_SWITCH", 1),
    settingIntent("SETTING_INFO", 2),

    // User profile preferences

    userAuth("USER_AUTH"),
    userName("USER_NAME"),
    userLastName("USER_LAST_NAME"),
    userPhone("USER_PHONE"),
    userPhoneAccepted("USER_PHONE_ACCEPTED"),
    userEmail("USER_EMAIL"),
    userEmailAccepted("USER_EMAIL_ACCEPTED"),
    userAddress("USER_ADDRESS"),

    // Help constants

    errorDescription("ERROR")

}
