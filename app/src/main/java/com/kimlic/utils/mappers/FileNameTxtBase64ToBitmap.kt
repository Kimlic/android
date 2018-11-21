package com.kimlic.utils.mappers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.IOException

/*
* This util class gets file name from app folder directory
 * */

class FileNameTxtBase64ToBitmap(private val context: Context) : BaseMapper<String, Bitmap?> {

    // life

    override fun transform(input: String): Bitmap? {
        val fileByteArrayBase64Uri = Uri.parse(context.filesDir.toString() + "/" + input)
        return transformByUri(fileByteArrayBase64Uri)
    }

    fun transformByUri(uri: Uri): Bitmap? {
        return try {
            val fileByteArrayBase64 = File(uri.path).readBytes()
            val decodedString = Base64.decode(fileByteArrayBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: IOException) {
            Log.d("MAPPER", "io exception!!!")
            null
        }
    }
}