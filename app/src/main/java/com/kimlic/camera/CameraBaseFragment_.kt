@file:Suppress("DEPRECATION")

package com.kimlic.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback
import io.fotoapparat.Fotoapparat
import io.fotoapparat.characteristic.LensPosition
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.LensPositionSelector
import kotlinx.android.synthetic.main.fragment_document_card_.*
import java.io.File

abstract class CameraBaseFragment_ : BaseFragment() {

    // Constants

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1000
    }

    // Binding

    @BindView(R.id.captureBt)
    lateinit var captureBt: Button

    // Variables

    private var cameraId = 0
    private lateinit var callback: PhotoCallback
    private lateinit var fotoapparat: Fotoapparat


    // Live

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_document_card_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    , REQUEST_CAMERA_PERMISSION)
            return
        }
        fotoapparat.start()
    }

    override fun onPause() {
        super.onPause()
        fotoapparat.stop()
    }


    // Public

    fun setCallback(callback: PhotoCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        cameraId = arguments!!.getInt(AppConstants.CAMERA_TYPE.key, AppConstants.CAMERA_REAR.intKey)


//        Fotoapparat.with(activity!!.applicationContext)
//                .into(cameraView)           // view which will draw the camera preview
//                .previewScaleType(ScaleType.CENTER_CROP)  // we want the preview to fill the view
//                .photoSize(biggestSize())   // we want to have the biggest photo possible     .lensPosition(back())       // we want back camera     .focusMode(firstAvailable(  // (optional) use the first focus mode which is supported by device             continuousFocus(),             autoFocus(),        // in case if continuous focus is not available on device, auto focus will be used             fixed()             // if even auto focus is not available - fixed focus mode will be used     ))     .flash(firstAvailable(      // (optional) similar to how it is done for focus mode, this time for flash             autoRedEye(),             autoFlash(),             torch()     ))     .frameProcessor(myFrameProcessor)   // (optional) receives each frame from preview stream     .logger(loggers(            // (optional) we want to log camera events in 2 places at once             logcat(),           // ... in logcat             fileLogger(this)    // ... and to file     ))     .build();


        fotoapparat = Fotoapparat.with(activity!!.applicationContext)
                .into(cameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .build()

        captureBt.setOnClickListener {
            val results = fotoapparat.takePicture()
            results.saveToFile(File(activity!!.filesDir.toString() + "/" + "ffffff"))

            confirmLl.visibility = View.VISIBLE
            auxilaryContourIv.visibility = View.INVISIBLE
        }
    }


    // Permissions

    private fun permissionsGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    , REQUEST_CAMERA_PERMISSION)
            return false
        }
        return true
    }

    // Private helpers

    private fun showResultPhoto(data: ByteArray?) {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
        captureBt.visibility = View.GONE
        previewIv.setImageBitmap(rotateBitmap(bitmap, if (cameraId == 1) -90f else 90f))
        confirmLl.visibility = View.VISIBLE

        confirmBt.setOnClickListener { }//closeCamera(); callback.callback(data) }

        retakelBt.setOnClickListener {
            confirmLl.visibility = View.GONE
            captureBt.visibility = View.VISIBLE
        }
    }


    private fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}