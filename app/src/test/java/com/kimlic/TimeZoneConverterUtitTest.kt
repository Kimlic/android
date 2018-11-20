package com.kimlic

import com.kimlic.utils.time_converter.TimeZoneConverter
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeZoneConverterUnitTest {

    // Constants

    companion object {
        private const val MILLIS_TEST_DATE = 1542722201517//20 Nov 2018
        private const val STRING_TEST_DATE = "20 Nov 2018"
        private const val STRING_UNIX_STAMP = "2018-10-17T15:33:20.761613Z"
        private const val LONG_FROM_UNIX_STAMP_SECONDS = 1539779600L
    }

    @Test
    fun convertMillisToDateTest() {
        val convertedDate = TimeZoneConverter().convertMillisToDateString(millis = MILLIS_TEST_DATE)
        Assert.assertEquals(convertedDate, STRING_TEST_DATE)
    }

    @Test
    fun convertSecondsToDateTest() {
        val convertedDate = TimeZoneConverter().convertSecondsToDateString(seconds = MILLIS_TEST_DATE / 1000)
        Assert.assertEquals(convertedDate, STRING_TEST_DATE)
    }

    @Test
    fun convertTimeStampToSecondsTest() {
        val convertedSeconds = TimeZoneConverter().convertToSeconds(timeDate = STRING_UNIX_STAMP)
        assertEquals(convertedSeconds, LONG_FROM_UNIX_STAMP_SECONDS)
    }

    @Test
    fun convertTimeStampToMillisTest() {
        val convertedMillis = TimeZoneConverter().convertToMillis(timeDate = STRING_UNIX_STAMP)
        assertEquals(convertedMillis, LONG_FROM_UNIX_STAMP_SECONDS * 1000)
    }
}