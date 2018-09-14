package com.kimlic.documents

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.utils.Cache
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import kotlinx.android.synthetic.main.activity_verify_details_single.*
import java.io.File
import java.util.*

/*  Activity has next states
*   -   Preview
*               - preview document:   - accepted or in progress all fields aren't available and country is shown. Button is hidden
*               - preview document:
*                                     - user hasn't document in progress - firstName and lastName fields are available to modify
*                                     - user has document in progress - firstName and Last name are Available to modify
*   -   Send    send document
*                                     - user have document in progress - name and lastName aren't available
*                                     - user hasn't document in progress - all fields are editable.
* */

class DocumentDetails_ : BaseActivity() {

    // Binding
//
    @BindViews(R.id.numberEt, R.id.expireDateEt)
    lateinit var textFields: List<@JvmSuppressWildcards EditText>

    // Variables


    private lateinit var model: ProfileViewModel
    private lateinit var documentType: String
    private lateinit var country: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_details_single)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        ButterKnife.bind(this)
        setupUI()
    }

    // Private

    private fun setupUI() {
        documentType = intent.extras.getString(AppConstants.DOCUMENT_TYPE.key, "")
        country = intent.extras.getString("country", "")

        setupTitle()
        fillPhotoFrames()

        expireDateEt.setOnClickListener { datePicker() }
        backBt.setOnClickListener { finish() }

        saveBt.setOnClickListener {
            if (validFields()) {
                val portraitByteArray = File(filesDir.toString() + "/${Cache.PORTRAIT.file}").readBytes()
                val frontByteArray = File(filesDir.toString() + "/${Cache.FRONT.file}").readBytes()
                val backByteArray = File(filesDir.toString() + "/${Cache.BACK.file}").readBytes()

                val expireDate = expireDateEt.text.toString()
                val documentNumber = numberEt.text.toString()

                model.saveDocumentAndPhoto(documentType, country, documentNumber, expireDate, portraitByteArray, frontByteArray, backByteArray)

                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }


    private fun fillPhotoFrames() {
        portraitIv.setImageBitmap(rotateBitmap(FileNameTxtBase64ToBitmap().transform(Cache.PORTRAIT.file)!!, -90f))
        frontIv.setImageBitmap(cropped(Cache.FRONT.file))
        backIv.setImageBitmap(cropped(Cache.BACK.file))
    }

    private fun setupTitle() {
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
}