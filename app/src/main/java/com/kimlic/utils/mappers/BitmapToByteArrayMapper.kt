package com.kimlic.utils.mappers

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class BitmapToByteArrayMapper : BaseMapper<Bitmap, ByteArray> {

    // Life

    override fun transform(input: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        input.compress(Bitmap.CompressFormat.JPEG, 40, stream)
        return stream.toByteArray()
    }
}