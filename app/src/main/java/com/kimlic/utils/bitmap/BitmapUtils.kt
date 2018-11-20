package com.kimlic.utils.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapUtils {

    fun getScaledBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int, filter: Boolean) = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, filter)

    fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}