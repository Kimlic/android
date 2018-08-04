package com.kimlic.utils.mappers

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ByteArrayToBitmapMapper : BaseMapper<ByteArray, Bitmap> {

    // Live

    override fun transform(input: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(input, 0, input.size)
    }
}