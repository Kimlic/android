package com.kimlic.verification

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ImageView
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.utils.UserPhotos
import kotlinx.android.synthetic.main.activity_verify_document.*
import java.io.File

class DocumentVerifyChooseActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_document)

        setupUI()
    }

    private fun setupUI() {
        passportBt.setOnClickListener { PresentationManager.verifyPassport(this@DocumentVerifyChooseActivity) }
        driversBt.setOnClickListener { PresentationManager.verifyDriverLicence(this@DocumentVerifyChooseActivity) }
        idCardBt.setOnClickListener { PresentationManager.verifyIDCard(this@DocumentVerifyChooseActivity) }

        backBt.setOnClickListener { finish() }
        setupBackground()
    }

    private fun setupBackground() {
        val filePath = this.applicationContext.filesDir.toString() + "/" + UserPhotos.portraitFilePath.fileName
        val file = File(filePath)

        rootIv.scaleType = ImageView.ScaleType.CENTER_CROP
        if (file.exists()) rootIv.setImageBitmap(croped(UserPhotos.portraitFilePath.fileName))
    }

    private fun croped(fileName: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(this.applicationContext.filesDir.toString() + "/" + fileName)
        val rotated = rotateBitmap(bitmap, -90f)
        val width = rotated.width
        val height = rotated.height
        val bitmapCroped = Bitmap.createBitmap(rotated, (0.22 * width).toInt(), (0.1 * height).toInt(), (0.5 * width).toInt(), (0.7 * height).toInt())

        return bitmapCroped
    }

    private fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix(); matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}