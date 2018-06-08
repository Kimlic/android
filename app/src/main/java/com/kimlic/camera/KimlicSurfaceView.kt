@file:Suppress("DEPRECATION")

package com.kimlic.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

@SuppressLint("ViewConstructor")
class KimlicSurfaceView(context: Context, val camera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    // Variables

    private val surfaceHolder: SurfaceHolder = holder

    // Init

    init {
        surfaceHolder.addCallback(this)
    }

    // SurfaceHolder.BaseCallback implementation

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            camera.setPreviewDisplay(surfaceHolder)
        } catch (e: IOException) {
            throw Exception("Can't create surface view")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {}

}