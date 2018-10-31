package com.kimlic.utils.time_converter

import java.text.SimpleDateFormat
import java.util.*

class TimeZoneConverter {

    // Public

    fun convertToMillis(timeDate: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timeDate)
        return date.time
    }

    fun convertToSeconds(timeDate: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timeDate)
        return date.time / 1000
    }

    fun convertMillToDateString(millis: Long): String {
        val date = Date(millis * 1000)
        val sdf = SimpleDateFormat("dd mmm YYYY", Locale("en", "US"))
        return sdf.format(date)
    }
}