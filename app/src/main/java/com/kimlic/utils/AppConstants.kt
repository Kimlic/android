package com.kimlic.utils

enum class AppConstants(val key: String, val intKey: Int = -1) {

    APP_PREFERENCES("KIMLIC_PREFERENCES"),

    AUTH("AUTH"),
    USER_ID("USERID"),
    ACCOUNT_ADDRESS("ACCOUNT_ADDRESS"),

    PASSCODE("PASSCODE"),

    IS_PASSCODE_ENABLED("IS_PASSCODE_ENABLED"),
    IS_PASSCODE_OFFERED("IS_PASSCODE_OFFERED"),
    IS_TOUCH_ENABLED("IS_TOUCH_ENABLED"),
    IS_RECOVERY_ENABLED("IS_RECOVERY_ENABLED"),
    IS_RECOVERY_OFFERED("IS_RECOVERY_OFFERED"),

    IS_GOOGLE_SIGNE_IN("IS_GOOGLE_SIGNE_IN"),
    IS_DRIVE_ACTIVE("IS_GOOGLE_DRIVE_ACTIVE"),

    TERMS("TERMS"),
    PRIVACY("PRIVACY"),

    TUTORIALS("TUTORIALS"),
    FINGERPRINT("FINGERPRINT"),

    // Camera

    CAMERA_TYPE("CAMERA_TYPE"),
    CAMERA_FRONT("CAMERA_FRONT", 1),
    CAMERA_REAR("CAMERA_REAR", 0),

    // Document type

    DOCUMENT_TYPE("DOCUMENT_TYPE"),
    DOCUMENT_TO_VERIFY("DOCUMENT_TO_VERIFY"),
    DOCUMENT_BYTE_ARRAY("DOCUMENT_BYTE_ARRAY"),

    // Document status
    VERIFIED("verified"),
    UNVERIFIED("unverified"),
    // Photo type

    PHOTO_FACE_TYPE("face"),
    PHOTO_FRONT_TYPE("document-front"),
    PHOTO_BACK_TYPE("document-back"),
    PHOTO_ADDRESS_TYPE("address"),

    PORTRAIT_DOCUMENT("portrait_document"),


    COUNTRY("country"),
    TITLE("title"),
    SUBTITLE("subtitle"),

    // File path

    FILE_PATH_RESULT("FILE_PATH_RESULT"),

    // Settings type

    SETTINGS_SWITCH("SETTING_SWITCH", 1),
    SETTINGS_INTENT("SETTING_INFO", 2),

    // User profile preferences

    USER_AUTH("USER_AUTH"),

    // Help constants

    ERROR_DESCRIPTION("ERROR")
}