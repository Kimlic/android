package com.kimlic.documents

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.utils.UserPhotos
import kotlinx.android.synthetic.main.activity_verify_details.*

class VerifyDetails : BaseActivity() {

    // Variables

    private lateinit var verifyTraget: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_details)

        setupUI()
    }

    // Private

    private fun setupUI() {
        verifyTraget = intent.extras.getString(AppConstants.documentType.key, "")
        filData()

        backBt.setOnClickListener { finish() }

        addBt.setOnClickListener {

            if (validFields())
                finish()
            // Handle Data

        }
    }


    private fun filData() {
        when (verifyTraget) {
            AppConstants.documentPassport.key -> {
                titleTv.text = getString(R.string.passport)
                frontIv.setImageBitmap(croped(UserPhotos.passportFrontSide.fileName))
                backIv.setImageBitmap(croped(UserPhotos.passportBackSide.fileName))
            }
            AppConstants.documentLicense.key -> {
                titleTv.text = getString(R.string.driver_licence)
                frontIv.setImageBitmap(croped(UserPhotos.driverLicensFrontSide.fileName))
                backIv.setImageBitmap(croped(UserPhotos.driverLicensBackSide.fileName))
            }
            AppConstants.documentID.key -> {
                titleTv.text = getString(R.string.id_card)
                frontIv.setImageBitmap(croped(UserPhotos.IDCardFrontSide.fileName))
                backIv.setImageBitmap(croped(UserPhotos.IDCardBackSide.fileName))
            }
            else -> throw Exception("Wrong document type")
        }
    }

    // Private helpers

    private fun validFields(): Boolean {
        var noError = true

        if (documentEt.text.length < 3) {
            documentEt.setError("error")
            noError = false
        } else {
            documentEt.setError(null)
            noError = true
        }

        if (expireDateEt.text.length < 3) {
            expireDateEt.setError("error")
            noError = false
        } else {
            expireDateEt.setError(null)
            noError = true
        }

        if (countryEt.text.length < 3) {
            countryEt.setError("error")
            noError = false
        } else {
            countryEt.setError(null)
            noError = true
        }

        return noError
    }

    private fun croped(fileName: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(this.applicationContext.filesDir.toString() + "/" + fileName)
        val originalbitmap = rotateBitmap(bitmap, 90f)
        val width = originalbitmap.width
        val height = originalbitmap.height
        val bitmapCroped = Bitmap.createBitmap(originalbitmap, (0.15 * width).toInt(), (0.22 * height).toInt(), (0.7 * width).toInt(), (0.35 * height).toInt())

        return bitmapCroped
        //frontIv.setImageBitmap(bitmapCroped)
    }

    private fun rotateBitmap(sourse: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angel)
        return Bitmap.createBitmap(sourse, 0, 0, sourse.width, sourse.height, matrix, true)
    }
}
