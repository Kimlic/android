package com.kimlic.documents

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.Cache
import com.kimlic.utils.bitmap.BitmapUtils
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
        documentType = intent?.extras?.getString(AppConstants.DOCUMENT_TYPE.key, "").orEmpty()
        country = intent?.extras?.getString("country", "").orEmpty()
        action = intent?.extras?.getString("action", "preview").orEmpty()

        when (action) {
            "preview" -> setupPreview()
            "previewAndSave" -> setupPreviewAndSave()
            //"choose" -> setupChoose()
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

        statusIv.visibility = View.GONE
        saveBt.text = getString(R.string.ok)
        saveBt.setOnClickListener { finish() }
        backBt.setOnClickListener { finish() }

        when (currentDocument.state) {
            "" -> {// status added in create new document
                spannedStatus(getString(R.string.verification, "Created"))
            }
            Status.CREATED.state -> {// status from quorum verification
                spannedStatus(getString(R.string.verification, "Pending"))
            }
            Status.VERIFIED.state -> {
                spannedStatus(getString(R.string.verification, "Verified"))
            }
            Status.UNVERIFIED.state -> {
                statusIv.visibility = View.VISIBLE
                spannedStatus(getString(R.string.verification, "Unverified"))
                saveBt.text = getString(R.string.add_new_id)
                saveBt.setOnClickListener {
                    model.deleteDocument(documentId = currentDocument.id)
                    finish()
                    PresentationManager.documentChoiseVerify(this)
                }
            }
        }
    }

    private fun setupPreviewAndSave() {
        setupTitle(documentType)
        fillPhotoFramesFromCache()
        statusLl.visibility = View.GONE

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
        portraitIv.setImageBitmap(BitmapUtils.rotateBitmap(FileNameTxtBase64ToBitmap().transform(Cache.PORTRAIT.file)!!, 0f))//-90f Previous value with old camera
        frontIv.setImageBitmap(cropped(Cache.FRONT.file))
        if (documentType != AppDoc.PASSPORT.type) {
            backIv.setImageBitmap(cropped(Cache.BACK.file))
        } else {
            backSideTv.visibility = View.GONE
            backFl.visibility = View.GONE
        }

    }

    private fun filPhotoFramesPreview(photos: Map<String, String>) {
        portraitIv.setImageBitmap(BitmapUtils.rotateBitmap(FileNameTxtBase64ToBitmap().transform(photos[AppConstants.PHOTO_FACE_TYPE.key]!!)!!, 0f))//-90f Previous value with old camera
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

    private fun spannedStatus(text: String) {
        val words = text.split(" ")
        val spanStart = words[0].length + 1
        val spannableBuilder = SpannableStringBuilder(text)
        val boldStyle = StyleSpan(Typeface.BOLD)

        spannableBuilder.setSpan(boldStyle, spanStart, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        statusTv.text = spannableBuilder
    }

    private fun cropped(fileName: String): Bitmap {
        val bitmap = FileNameTxtBase64ToBitmap().transform(fileName)
        val originalBitmap = BitmapUtils.rotateBitmap(bitmap!!, 0f)//-90f Previous value with old camera
        val width = originalBitmap.width
        val height = originalBitmap.height
        return Bitmap.createBitmap(originalBitmap, (0.12 * width).toInt(), (0.3 * height).toInt(), (0.76 * width).toInt(), (0.37 * height).toInt())
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, R.style.DatePickerStyle, DatePickerDialog.OnDateSetListener { _, year_, monthOfYear, dayOfMonth ->
            expireDateEt.text = Editable.Factory.getInstance().newEditable("$dayOfMonth - ${monthOfYear+1} - $year_")
        }, year, month, day)
        dialog.datePicker.minDate = Date().time
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.show()
    }

    private fun disableEditing(views: List<EditText>) =
            views.forEach { it.isClickable = false; it.isFocusableInTouchMode = false; it.isFocusable = false; it.isCursorVisible = false }
}