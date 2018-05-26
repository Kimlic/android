package com.kimlic.utils

enum class AppConstants(val key: String, val intKey: Int = -1) {
    appPreferences("KIMLIC_PREFERENCES"),
    passcode("PASSCODE"),
    tutorials("TUTORIALS"),
    fingerprint("FINGERPRINT"),

    cameraFront("CAMERA_FRONT", 1),
    cameraRear("CAMERA_REAR", 0)
}
