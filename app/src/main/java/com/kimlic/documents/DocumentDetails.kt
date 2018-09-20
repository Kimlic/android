package com.kimlic.documents

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.Cache
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import kotlinx.android.synthetic.main.activity_verify_details.*
import java.util.*

class DocumentDetails : BaseActivity() {

    // Binding

    @BindViews(R.id.numberEt, R.id.expireDateEt)
    lateinit var textFields: List<@JvmSuppressWildcards EditText>

    // Variables


    private lateinit var model: ProfileViewModel
    private lateinit var documentType: String
    private lateinit var country: String
    private lateinit var action: String

    private lateinit var user: User
    private lateinit var photosMap: Map<String, String>

    private lateinit var currentDocument: Document

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_details)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        ButterKnife.bind(this)
        setupUI()
    }

    // Private

    private fun setupUI() {
        documentType = intent.extras.getString(AppConstants.DOCUMENT_TYPE.key, "")
        country = intent.extras.getString("country", "")
        action = intent.extras.getString("action", "preview")

        when (action) {
            "preview" -> setupPreview()
            "previewAndSave" -> setupPreviewAndSave()
        }
    }

    private fun setupPreview() {
        currentDocument = model.userDocument(documentType)!!
        user = model.user()
        photosMap = model.userDocumentPhotos(documentType).map { it.type to it.file }.toMap()

        setupTitle(documentType)
        filPhotoFramesPreview(photosMap)

        disableEditing(textFields.subList(0, 2))

        numberEt.text = Editable.Factory.getInstance().newEditable(currentDocument.number)
        expireDateEt.text = Editable.Factory.getInstance().newEditable(currentDocument.expireDate)
        countryEt.text = Editable.Factory.getInstance().newEditable(currentDocument.country)

        saveBt.text = getString(R.string.ok)
        saveBt.setOnClickListener { finish() }
        backBt.setOnClickListener { finish() }
    }

    private fun setupPreviewAndSave() {
        setupTitle(documentType)
        fillPhotoFramesFromCache()

        saveBt.setOnClickListener {
            if (validFields()) {
                saveBt.isClickable = false

                val expireDate = expireDateEt.text.toString()
                val documentNumber = numberEt.text.toString()

                model.saveDocumentAndPhoto(documentType, country, documentNumber, expireDate)
                successful()
            }
        }
        expireDateEt.setOnClickListener { hideKeyboard();datePicker() }
        backBt.setOnClickListener { finish() }
    }

    private fun fillPhotoFramesFromCache() {
        portraitIv.setImageBitmap(rotateBitmap(FileNameTxtBase64ToBitmap().transform(Cache.PORTRAIT.file)!!, -90f))
        frontIv.setImageBitmap(cropped(Cache.FRONT.file))
        if (documentType != AppDoc.PASSPORT.type) {
            backIv.setImageBitmap(cropped(Cache.BACK.file))
        } else {
            backSideTv.visibility = View.GONE
            backFl.visibility = View.GONE
        }

    }

    private fun filPhotoFramesPreview(photos: Map<String, String>) {
        portraitIv.setImageBitmap(rotateBitmap(FileNameTxtBase64ToBitmap().transform(photos[AppConstants.PHOTO_FACE_TYPE.key]!!)!!, -90f))
        frontIv.setImageBitmap(cropped(photos[AppConstants.PHOTO_FRONT_TYPE.key]!!))
        if (currentDocument.type != AppDoc.PASSPORT.type) {
            backIv.setImageBitmap(cropped(photos[AppConstants.PHOTO_BACK_TYPE.key]!!))
        } else {
            backSideTv.visibility = View.GONE
            backFl.visibility = View.GONE
        }
    }

    private fun setupTitle(documentType: String) {
        titleTv.text =
                when (documentType) {
                    AppDoc.PASSPORT.type -> getString(R.string.passport)
                    AppDoc.DRIVERS_LICENSE.type -> getString(R.string.driver_license)
                    AppDoc.ID_CARD.type -> getString(R.string.id_card)
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> getString(R.string.residence_permit)
                    AppDoc.SOCIAL_SECURITY_CARD.type -> getString(R.string.social_security_card)
                    AppDoc.BIRTH_CERTIFICATE.type -> getString(R.string.birth_certificate)
                    else -> {
                        throw Exception("Wrong document type")
                    }
                }
        countryEt.text = Editable.Factory.getInstance().newEditable(country)
    }

    private fun successful() {
        val fragment = DocumentSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                val intent = Intent()
                // Send added document to Account Activity
                intent.putExtra(AppConstants.DOCUMENT_TYPE.key, documentType)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        fragment.show(supportFragmentManager, DocumentSuccessfulFragment.FRAGMENT_KEY)
    }

    // Private helpers

    private fun validFields(): Boolean {
        val error = textFields.map {
            if (it.text.length < 3) {
                it.error = getString(R.string.error); false
            } else {
                it.error = null; true
            }
        }.contains(false)
        return !error
    }

    private fun cropped(fileName: String): Bitmap {
        val bitmap = FileNameTxtBase64ToBitmap().transform(fileName)
        val originalBitmap = rotateBitmap(bitmap!!, 90f)
        val width = originalBitmap.width
        val height = originalBitmap.height
        return Bitmap.createBitmap(originalBitmap, (0.15 * width).toInt(), (0.22 * height).toInt(), (0.7 * width).toInt(), (0.35 * height).toInt())
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, R.style.DatePickerStyle, DatePickerDialog.OnDateSetListener { _, year_, monthOfYear, dayOfMonth ->
            expireDateEt.text = Editable.Factory.getInstance().newEditable("$dayOfMonth - $monthOfYear - $year_")
        }, year, month, day)
        dialog.datePicker.minDate = Date().time
        dialog.show()
    }

    private fun disableEditing(views: List<EditText>) =
            views.forEach { it.isClickable = false; it.isFocusableInTouchMode = false; it.isFocusable = false; it.isCursorVisible = false }
}