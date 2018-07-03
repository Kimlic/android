@file:Suppress("DEPRECATION")

package com.kimlic.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kimlic.BaseFragment
import com.kimlic.KimlicApp
import com.kimlic.R
import com.kimlic.utils.AppConstants
import java.io.FileOutputStream

open class CameraBaseFragment : BaseFragment(), Camera.PictureCallback {

    // Constants

    val REQUEST_CAMERA_PERMISSION = 1000

    // Binding

    @BindView(R.id.frameLayout)
    lateinit var frameLayout: FrameLayout
    @BindView(R.id.captureBt)
    lateinit var captureBt: Button

    // Variables

    private var cameraId = 0
    private lateinit var camera: Camera
    private var kimlicSurfaceView: KimlicSurfaceView? = null
    private lateinit var filePath: String

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_id_photo, container, false)
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
                    arrayOf<String>(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    , REQUEST_CAMERA_PERMISSION)
            return
        }
        openCamera()
        kimlicSurfaceView = KimlicSurfaceView(KimlicApp.applicationContext(), camera)
        frameLayout.addView(kimlicSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        camera.release()
    }

    // Private

    private fun setupUI() {
        cameraId = arguments!!.getInt(AppConstants.cameraType.key, AppConstants.cameraRear.intKey)
        captureBt.setOnClickListener { takePicture() }
    }

    private fun takePicture() {
        camera.takePicture(null, null, null, this)
    }

    private fun openCamera() {
        if (!permissionsGranted()) return else {
            camera = Camera.open(cameraId)
            setCameraParams(camera)
            camera.startPreview()
        }
    }

    private fun closeCamera() {
        if (!permissionsGranted()) return

        camera.stopPreview()
        frameLayout.removeAllViews()
        kimlicSurfaceView = null
    }

    private fun setCameraParams(camera: Camera) {
        camera.setDisplayOrientation(90)
        val params = camera.parameters
        val sizes: List<Camera.Size> = params.supportedPictureSizes
        var currentWidth = 640
        var currentHight = 480

        for (size in sizes) {
            if (size.height > currentHight && size.width > currentWidth) {
                currentHight = size.height
                currentWidth = size.width
            }
        }
        params.pictureFormat = ImageFormat.JPEG
        params.focusMode = (Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
        params.setPictureSize(currentWidth, currentHight)
        camera.parameters = params
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

    // CameraPicture BaseCallback

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        // Save File
        // send file URI to activity

        val fos: FileOutputStream?
        val filepath = AppConstants.userStagePortraitFileName.key // temporary
        try {
            fos = KimlicApp.applicationContext().openFileOutput(filepath, Context.MODE_PRIVATE)
            fos.write(data)
            fos.close()
        } catch (e: Exception) {
            throw Exception("Cant write data to internal storage")
        }

        camera?.startPreview()
    }
}