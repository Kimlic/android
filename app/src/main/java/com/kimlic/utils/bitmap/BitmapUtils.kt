package com.kimlic.utils.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapUtils {

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int, angel: Float, isNecessaryToKeepOrig: Boolean): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        matrix.postRotate(angel)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)

        if (!isNecessaryToKeepOrig) bm.recycle()

        return resizedBitmap
    }

    fun getScaledBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int, filter: Boolean) = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, filter)

    fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}