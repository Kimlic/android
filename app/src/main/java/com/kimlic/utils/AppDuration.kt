package com.kimlic.utils

enum class AppDuration(val duration: Long, val repeat: Int = 4) {

    SUCCESSFULL_DURATION(2000, 4),
    BLOCKCHAIN_DURATION(1000, 4),


    //quick shake logo duration
    LOGO_DURATION(800),
    FINGERPRINT_DURATION(4000, 10),
    SPLASH(1500, 1)

}