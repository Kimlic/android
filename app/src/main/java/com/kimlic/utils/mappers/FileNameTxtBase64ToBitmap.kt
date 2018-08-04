package com.kimlic.utils.mappers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.kimlic.KimlicApp
import java.io.File
import java.io.IOException

class FileNameTxtBase64ToBitmap : BaseMapper<String, Bitmap?> {

    // life

    override fun transform(input: String): Bitmap? {
        try {
            val fileByteArrayBase64 = File(KimlicApp.applicationContext().filesDir.toString() + "/" + input).readBytes()
            val decodedString = Base64.decode(fileByteArrayBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            return bitmap
        } catch (e: IOException) {
            Log.d("MAPPER", "io exception!!!")
            return null
        }
    }
}