@file:Suppress("DEPRECATION")

package com.kimlic.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import java.io.IOException

@SuppressLint("ViewConstructor")
class KimlicSurfaceView(context: Context, val camera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    // Variables

    private val surfaceHolder: SurfaceHolder = holder

    // Init

    init {
        surfaceHolder.addCallback(this)
    }

    // Life

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            camera.setPreviewDisplay(surfaceHolder)
        } catch (e: IOException) {
            throw Exception("Can't create surface view")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {}

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val manager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
//        Log.d("TAGSIZE", "width = $width")
//        Log.d("TAGSIZE", "height = $height")
        setMeasuredDimension(width, height)
    }
}