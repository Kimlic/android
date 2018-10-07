@file:Suppress("DEPRECATION")

package com.kimlic.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback
import com.kimlic.utils.bitmap.BitmapUtils
import com.kimlic.utils.mappers.BitmapToByteArrayMapper
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import io.fotoapparat.selector.highestResolution
import io.fotoapparat.selector.manualJpegQuality
import kotlinx.android.synthetic.main.fragment_document_card.*

abstract class CameraBaseFragment : BaseFragment() {

    // Constants

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1000
        private const val PHOTO_SIZE_VALUE = 1500
    }

    // Binding

    @BindView(R.id.captureBt)
    lateinit var captureBt: Button

    // Variables

    private var cameraId = 0
    private lateinit var callback: PhotoCallback
    private lateinit var fotoapparat: Fotoapparat
    private lateinit var mediaPlayer: MediaPlayer

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

        if (!permissionsGranted()) return
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
        mediaPlayer = MediaPlayer.create(activity, R.raw.camera_shutter_click_)
        mediaPlayer.setVolume(100f, 100f)

        fotoapparat = Fotoapparat.with(activity!!.applicationContext)
                .into(cameraView)
                .lensPosition(if (cameraId == AppConstants.CAMERA_REAR.intKey) back() else front())
                .previewScaleType(ScaleType.CenterCrop)
                .photoResolution(highestResolution())
                .jpegQuality(manualJpegQuality(90))
                .build()

        captureBt.setOnClickListener {
            captureBt.isClickable = false
            mediaPlayer.start()
            fotoapparat.focus()
            val results = fotoapparat.takePicture()
            results
                    .toBitmap()//scaled(1f)
                    .whenAvailable { bitmapPhoto ->
                        fotoapparat.stop()
                        showResultPhoto(bitmapPhoto!!.bitmap, -bitmapPhoto.rotationDegrees.toFloat())
                    }
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

    private fun showResultPhoto(bitmap: Bitmap, angle: Float) {
        captureBt.visibility = View.GONE
        previewIv.visibility = View.VISIBLE
        auxilaryContourIv.visibility = View.INVISIBLE
        documenTitleTv.visibility = View.GONE
        documentTypeIv.visibility = View.GONE

        confirmLl.visibility = View.VISIBLE

        val bitmapRotated = BitmapUtils.rotateBitmap(bitmap, angle)

        previewIv.setImageBitmap(bitmapRotated)
        previewIv.scaleType = ImageView.ScaleType.CENTER_CROP

        confirmBt.setOnClickListener {
            val height = bitmapRotated.height
            val width = bitmapRotated.width
            callback.callback(data = BitmapToByteArrayMapper().transform(BitmapUtils.getScaledBitmap(bitmapRotated, PHOTO_SIZE_VALUE, ((height / width.toFloat()) * PHOTO_SIZE_VALUE).toInt(), false)))
        }

        retakelBt.setOnClickListener {
            previewIv.visibility = View.GONE
            fotoapparat.start()
            auxilaryContourIv.visibility = View.VISIBLE
            captureBt.visibility = View.VISIBLE
            confirmLl.visibility = View.GONE
            captureBt.isClickable = true
        }
    }
}