package com.kimlic

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.test.runner.AndroidJUnit4
import com.kimlic.utils.bitmap.BitmapUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapUtilsUnitTest {

    // Constants

    companion object {
        private const val BITMAP_WIDTH = 320
        private const val BITMAP_HEIGHT = 240
    }

    // Variables

    private var bitmap: Bitmap? = null

    @Before
    fun setup() {
        bitmap = bitmap(BITMAP_WIDTH, BITMAP_HEIGHT)
    }

    @Test
    fun rotateBitmapTest() {
        val rotatedBitmap = BitmapUtils.rotateBitmap(bitmap!!, -90F)
        assertEquals(rotatedBitmap.height, BITMAP_WIDTH)
        assertEquals(rotatedBitmap.width, BITMAP_HEIGHT)
    }

    @Test
    fun getScaledBitmap() {
        val bitmapScaled2 = BitmapUtils.getScaledBitmap(bitmap!!, BITMAP_WIDTH * 2, BITMAP_HEIGHT * 2, false)
        assertEquals(bitmapScaled2.width, BITMAP_WIDTH * 2)
        assertEquals(bitmapScaled2.height, BITMAP_HEIGHT * 2)
    }

    @After
    fun clear() {
        bitmap = null
    }

    // Private

    private fun bitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }
}