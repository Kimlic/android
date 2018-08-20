package com.kimlic.documents

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.view.View
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_verify_details.*
import java.util.*

class DocumentDetails : BaseActivity() {

    // Binding

    @BindViews(R.id.firstNameEt, R.id.lastNameEt, R.id.numberEt, R.id.expireDateEt)
    lateinit var textFields: List<@JvmSuppressWildcards EditText>

    // Variables

    private var timer: CountDownTimer? = null
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null

    private lateinit var model: ProfileViewModel
    private lateinit var vendorsModel: VendorsViewModel
    private lateinit var user: User
    private lateinit var photosMap: Map<String, String>
    private lateinit var documentType: String
    private lateinit var currentDocument: Document
    private lateinit var country: String
    private lateinit var url: String
    private lateinit var action: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_details)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        ButterKnife.bind(this)
        setupUI()
    }

    // Private

    private fun setupUI() {
        initExtraVariables()
        fillData(user = user, photos = photosMap, document = currentDocument)

        when (action) {
            "send" -> {
                countryTil.visibility = View.VISIBLE
                countryEt.text = Editable.Factory.getInstance().newEditable(country)

                addBt.text = getString(R.string.verify_document)
                addBt.setOnClickListener {
                    if (validFields()) {
                        addBt.isClickable = false
                        updateUser()
                        updateDocument()
                        sendDocument()
                    }
                }
            }
            "preview" -> {
                addBt.text = getString(R.string.add_details)
                countryTil.visibility = View.GONE
                addBt.setOnClickListener {
                    if (validFields()) {
                        updateDocument()
                        updateUser()
                        finish()
                    }
                }
            }
        }
        backBt.setOnClickListener { finish() }
        expireDateEt.setOnClickListener { datePicker() }
    }

    private fun initExtraVariables() {
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        action = intent.extras.getString("action", "preview")
        country = intent.extras.getString("country", "")
        url = intent.extras.getString("url", "")

        currentDocument = model.userDocument(documentType)
        user = model.user(Prefs.currentAccountAddress)
        photosMap = model.userDocumentPhotos(documentType = documentType).map { it.type to it.file }.toMap()
    }

    private fun sendDocument() {
        showProgress()
        model.senDoc(docType = documentType, country = country, url = url,
                onSuccess = {
                    hideProgress()
                    showPopup("Success!", "Document sent!")
                    currentDocument.state = "pending"
                    model.updateDocument(currentDocument) //finish ()
                },
                onError = {
                    hideProgress()
                    addBt.isClickable = true
                    showPopup("Error", message = "Unable to proceed!")
                })
    }

    // Updates

    private fun updateUser() {
        user.firstName = firstNameEt.text.toString()
        user.lastName = lastNameEt.text.toString()
        model.updateUser(user)
    }

    private fun updateDocument() {
        currentDocument.number = numberEt.text.toString()
        currentDocument.expireDate = expireDateEt.text.toString()
        currentDocument.country = countryEt.text.toString()
        model.updateDocument(currentDocument)
    }

    private fun fillData(user: User, photos: Map<String, String>, document: Document) {
        frontIv.setImageBitmap(cropped(photos[AppConstants.photoFrontType.key]!!))
        backIv.setImageBitmap(cropped(photos[AppConstants.photoBackType.key]!!))

        firstNameEt.text = Editable.Factory.getInstance().newEditable(user.firstName)
        lastNameEt.text = Editable.Factory.getInstance().newEditable(user.lastName)
        numberEt.text = Editable.Factory.getInstance().newEditable(document.number)
        expireDateEt.text = Editable.Factory.getInstance().newEditable(document.expireDate)

        titleTv.text =
                when (document.type) {
                    AppDoc.PASSPORT.type -> getString(R.string.passport)
                    AppDoc.DRIVERS_LICENSE.type -> getString(R.string.driver_license)
                    AppDoc.ID_CARD.type -> getString(R.string.id_card)
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> getString(R.string.residence_permit)
                    else -> throw Exception("Wrong document type")
                }
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

    // Progress

    private fun showProgress() {
        timer = object : CountDownTimer(0, 0) {
            override fun onFinish() {
                blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
                blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer?.cancel()
    }

    override fun showPopup(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog?.dismiss() }
                .setCancelable(true)
                .create()
                .show()
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, R.style.DatePickerStyle, DatePickerDialog.OnDateSetListener { _, year_, monthOfYear, dayOfMonth ->
            expireDateEt.text = Editable.Factory.getInstance().newEditable("$dayOfMonth / $monthOfYear / $year_")
        }, year, month, day)
        dialog.datePicker.minDate = Date().time
        dialog.show()
    }
}