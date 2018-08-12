package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import com.android.volley.Response
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Photo
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import kotlinx.android.synthetic.main.activity_verify_details.*

class DocumentDetails : BaseActivity() {

    // Variables

    private lateinit var documentType: String
    private lateinit var accountAddres: String
    private lateinit var model: ProfileViewModel

    private lateinit var photoList: List<Photo>
    private lateinit var photoMap: Map<String, String>

    private var target: String = "preview"


    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_details)

        setupUI()
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        accountAddres = intent.extras.getString(AppConstants.accountAddress.key, "")
        target = intent.extras.getString("target", "preview")


        photoList = model.getUserDocumentPhotos(accountAddress = accountAddres, documentType = documentType)
        photoMap = photoList.map { it.type to it.file }.toMap()

        fillData(photos = photoMap, documentType = documentType)


        when (target) {
            "send" -> {
                addBt.setOnClickListener {
                    if (validFields()) {

                        val listNames = photoList.map { it.file }.toList()
//                        photoList.iterator().forEach {
//                            model.senDoc(it.file, onSuccess = {}, onError = {})
//                        }
                        model.senDoc(docType = documentType, onSuccess = {}, onError = {})
                    }
                }
            }
            "preview" -> {
//                addBt.text = "Ok"
//                addBt.setOnClickListener {
//                    if (validFields()) {
//                        Log.d("TAG", "preview clicked!!!!!!!!!!!!!!!!!!")
//
//
//                    }
//                }

                addBt.setOnClickListener {
                    if (validFields()) {

                        val listNames = photoList.map { it.file }.toList()
//                        photoList.iterator().forEach {
//                            model.senDoc(it.file, onSuccess = {}, onError = {})
//                        }
                        model.senDoc(docType = documentType, onSuccess = {}, onError = {})
                    }
                }
            }
        }


        backBt.setOnClickListener { finish() }
    }


    private fun fillData(photos: Map<String, String>, documentType: String) {
        frontIv.setImageBitmap(croped(photos.get("front")!!))
        backIv.setImageBitmap(croped(photos.get("back")!!))

        when (documentType) {
            AppConstants.documentPassport.key -> titleTv.text = getString(R.string.passport)
            AppConstants.documentLicense.key -> titleTv.text = getString(R.string.driver_licence)
            AppConstants.documentID.key -> titleTv.text = getString(R.string.id_card)
            AppConstants.documentPermit.key -> titleTv.text = getString(R.string.residence_permit)
            else -> throw Exception("Wrong document type")
        }
    }

    // Private helpers

    // @formatter:off
    private fun validFields(): Boolean {
        val error = getString(R.string.error)
        val docError = if (documentEt.text.length < 3) { documentEt.error = error; false } else { documentEt.error= null; true }
        val dateError =  if (expireDateEt.text.length < 3) { expireDateEt.error = error; false } else { expireDateEt.error = null; true }
        val countryError =  if (countryEt.text.length < 3) { countryEt.error = error; false  } else { countryEt.error = null; true}

        return (docError && dateError && countryError)
    }

    // @formatrter:on
    private fun croped(fileName: String): Bitmap {
        val bitmap = FileNameTxtBase64ToBitmap().transform(fileName)
        val originalbitmap = rotateBitmap(bitmap!!, 90f)
        val width = originalbitmap.width
        val height = originalbitmap.height
        val bitmapCroped = Bitmap.createBitmap(originalbitmap, (0.15 * width).toInt(), (0.22 * height).toInt(), (0.7 * width).toInt(), (0.35 * height).toInt())
        return bitmapCroped
    }
}
