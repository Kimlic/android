package com.kimlic.utils

interface PhotoCallback {
    fun callback(fileName: String, data: ByteArray)
}