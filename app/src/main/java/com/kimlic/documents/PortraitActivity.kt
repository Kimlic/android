package com.kimlic.documents

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.PortraitPhotoFragment
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback
import com.kimlic.utils.UserPhotos

class PortraitActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var fileName: String
    private lateinit var portraitBitmap: Bitmap
    private lateinit var portraitBitmapPreview: Bitmap

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portrait)

        setupUI()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        fileName = Prefs.currentAccountAddress + "_" + UserPhotos.stagePortrait.fileName
        initFragment()

        portraitFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName_: String, data: ByteArray) {
                portraitBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                portraitBitmapPreview = createPhotoPreview(portraitBitmap)

                saveBitmap(fileName, portraitBitmap)
                saveBitmap("preview_" + fileName, portraitBitmapPreview)

                model.addUserPhoto(Prefs.currentAccountAddress, fileName)
                model.addUserPhotoPreview(Prefs.currentAccountAddress, "preview_" + fileName)

                finish()
            }
        })
        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)
    }

    private fun initFragment() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        bundle.putString(AppConstants.filePathRezult.key, fileName)
        portraitFragment = PortraitPhotoFragment.newInstance(bundle)
    }

    private fun createPhotoPreview(originalBitmap: Bitmap): Bitmap {
        val resizedBitmap = getResizedBitmap(originalBitmap, 1024, 768, -90f, true)
        val width = resizedBitmap.width
        val height = resizedBitmap.height
        val cropedBitmap = Bitmap.createBitmap(resizedBitmap, (0.15 * width).toInt(), (0.12 * height).toInt(), (0.75 * width).toInt(), (0.7 * height).toInt())
        return cropedBitmap
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int, angel: Float, isNecessaryToKeepOrig: Boolean): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        matrix.postRotate(angel)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)

        if (!isNecessaryToKeepOrig) {
            bm.recycle()
        }
        return resizedBitmap
    }
}
