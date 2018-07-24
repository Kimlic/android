package com.kimlic.verification

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.KimlicApp
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.UserPhotos
import com.kimlic.verification.fragments.PortraitPhotoFragment
import java.io.File
import java.io.FileOutputStream

class PortraitActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var fileName: String

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
        fileName = Prefs.currentId.toString() + UserPhotos.stagePortrait.fileName
        initFragments()

        portraitFragment.setCallback(object : BaseCallback {
            override fun callback() {
                val user1 = KimlicDB.getInstance()!!.userDao().select(Prefs.currentId)
                user1.portraitFile = fileName // Name of user by it's id
                KimlicDB.getInstance()!!.userDao().update(user = user1)

                createPhotoPreview(fileName)
                finish()
            }
        })
        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        bundle.putString(AppConstants.filePathRezult.key, fileName)
        portraitFragment = PortraitPhotoFragment.newInstance(bundle)
    }

    private fun createPhotoPreview(filePath: String) {
        val path = filesDir.toString() + "/" + filePath
        val bitmapOriginal = BitmapFactory.decodeFile(path)// absolute path
        val resizedBitmap = getResizedBitmap(bitmapOriginal, 1024, 768, -90f, true)
        val vidth = resizedBitmap.width
        val height = resizedBitmap.height

        val cropedBitmap = Bitmap.createBitmap(resizedBitmap, (0.15 * vidth).toInt(), (0.12 * height).toInt(), (0.75 * vidth).toInt(), (0.7 * height).toInt())
        saveBitmap("preview_"+filePath, cropedBitmap)
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

    private fun saveBitmap(fileName: String, bitmap: Bitmap) {
        val fos: FileOutputStream?
        val file = File(KimlicApp.applicationContext().filesDir.toString() + "/" + fileName)
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            throw Exception("Can't write data to internal storage")
        }

    }
}
