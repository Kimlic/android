@file:Suppress("DEPRECATION")

package com.kimlic.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kimlic.BaseFragment
import com.kimlic.KimlicApp
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback
import kotlinx.android.synthetic.main.fragment_document_portrait.*

abstract class CameraBaseFragment : BaseFragment(), Camera.PictureCallback {

    // Constants

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1000
    }

    // Binding

    @BindView(R.id.frameLayout)
    lateinit var frameLayout: FrameLayout
    @BindView(R.id.captureBt)
    lateinit var captureBt: Button

    // Variables

    private var cameraId = 0
    private lateinit var camera: Camera
    private var kimlicSurfaceView: KimlicSurfaceView? = null
    private lateinit var callback: PhotoCallback

    // Live

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_document_card, container, false)
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

    // Public

    fun setCallback(callback: PhotoCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        cameraId = arguments!!.getInt(AppConstants.CAMERA_TYPE.key, AppConstants.CAMERA_REAR.intKey)
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
        var currentHeight = 480

        for (size in sizes) {
            if (size.height > currentHeight && size.width > currentWidth && size.height < 3000 && size.width < 2500) {
                currentHeight = size.height
                currentWidth = size.width
            }
        }

        params.pictureFormat = ImageFormat.JPEG
        params.jpegQuality = 80

        val supportedFocusMode = camera.parameters.supportedFocusModes
        val hasAutoFocus = supportedFocusMode != null && supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)

        if (hasAutoFocus) params.focusMode = (Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)

        params.setPictureSize(currentWidth, currentHeight)
        // setOptimalPreviewSize(params)
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

    private fun showResultPhoto(data: ByteArray?) {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
        captureBt.visibility = View.GONE
        previewIv.setImageBitmap(rotateBitmap(bitmap, if (cameraId == 1) -90f else 90f))
        confirmLl.visibility = View.VISIBLE

        confirmBt.setOnClickListener { closeCamera(); callback.callback(data) }

        retakelBt.setOnClickListener {
            confirmLl.visibility = View.GONE
            captureBt.visibility = View.VISIBLE
            openCamera()
            kimlicSurfaceView = KimlicSurfaceView(KimlicApp.applicationContext(), camera)
            frameLayout.addView(kimlicSurfaceView)
        }
    }

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        closeCamera()
        showResultPhoto(data)
    }

    private fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun setOptimalPreviewSize(params: Camera.Parameters) {
        val manager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        try {
            params.setPreviewSize(height, width)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}