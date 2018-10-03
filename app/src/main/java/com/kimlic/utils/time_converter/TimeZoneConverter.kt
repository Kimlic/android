package com.kimlic.utils.time_converter

import java.text.SimpleDateFormat
import java.util.*

class TimeZoneConverter {

    // Public

    fun covertToMillis(timeZone: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timeZone)
        return date.time
    }

    fun covertToSeconds(timeZone: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timeZone)
        return date.time / 1000
    }
}