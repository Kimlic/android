package com.kimlic.documents

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.view.View
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_verify_details.*
import java.util.*

class DocumentDetails : BaseActivity() {

    // Variables

    private lateinit var timer: CountDownTimer
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null

    private lateinit var model: ProfileViewModel
    private lateinit var vendorsModel: VendorsViewModel
    private lateinit var user: User
    private lateinit var photosMap: Map<String, String>
    private lateinit var documentType: String
    private lateinit var currentDocument: Document
    private lateinit var country: String
    private lateinit var url: String
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
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)

        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        target = intent.extras.getString("target", "preview")

        currentDocument = model.getUserDocument(documentType)
        user = model.getUser(Prefs.currentAccountAddress)
        photosMap = model.getUserDocumentPhotos(documentType = documentType).map { it.type to it.file }.toMap()


        fillData(user = user, photos = photosMap, document = currentDocument)

        country = intent.extras.getString("country", "")

        when (target) {
            "send" -> {
                url = intent.extras.getString("url", "")

                countryTil.visibility = View.VISIBLE
                countryEt.text = Editable.Factory.getInstance().newEditable(country)

                addBt.text = getString(R.string.verify_document)
                addBt.setOnClickListener {
                    if (validFields()) {
                        addBt.isClickable = false
                        progressBar.visibility = View.VISIBLE
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



    private fun sendDocument() {
        model.senDoc(docType = documentType, country = country, url = url,
                onSuccess = {
                    //hideProgress()
                    progressBar.visibility = View.GONE
                    showPopup("Success!", "Document sent!")
                    currentDocument.state = "pending"
                    model.updateDocument(currentDocument) //finish ()
                },
                onError = {
                    //hideProgress()
                    progressBar.visibility = View.GONE
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
        //countryEt.text = Editable.Factory.getInstance().newEditable(document.country)

        when (document.type) {
            AppConstants.documentPassport.key -> titleTv.text = getString(R.string.passport)
            AppConstants.documentLicense.key -> titleTv.text = getString(R.string.driver_license)
            AppConstants.documentID.key -> titleTv.text = getString(R.string.id_card)
            AppConstants.documentPermit.key -> titleTv.text = getString(R.string.residence_permit)
            else -> throw Exception("Wrong document type")
        }
    }

    // Private helpers

    // @formatter:off
    private fun validFields(): Boolean {
        val error = getString(R.string.error)
        val firstNameError = if (firstNameEt.text.length < 3) { firstNameEt.error = error; false } else { firstNameEt.error= null; true }
        val lastNameError = if (lastNameEt.text.length < 3) { lastNameEt.error = error; false } else { lastNameEt.error= null; true }
        val docError = if (numberEt.text.length < 3) { numberEt.error = error; false } else { numberEt.error= null; true }
        val dateError =  if (expireDateEt.text.length < 3) { expireDateEt.error = error; false } else { expireDateEt.error = null; true }
    // @formatrter:on
        return (docError && dateError && firstNameError && lastNameError)
    }

    private fun cropped(fileName: String): Bitmap {
        val bitmap = FileNameTxtBase64ToBitmap().transform(fileName)
        val originalBitmap = rotateBitmap(bitmap!!, 90f)
        val width = originalBitmap.width
        val height = originalBitmap.height
        val bitmapCropped = Bitmap.createBitmap(originalBitmap, (0.15 * width).toInt(), (0.22 * height).toInt(), (0.7 * width).toInt(), (0.35 * height).toInt())
        return bitmapCropped
    }

    // Progress

    private fun showProgress() {
        timer = object : CountDownTimer(100, 100) {
            override fun onFinish() {
                blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
                blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer?.let { it.cancel() }
    }

    override fun showPopup(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK)) { dialog, which -> dialog?.dismiss()}.setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    private fun datePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, R.style.DatePickerStyle, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            expireDateEt.text = Editable.Factory.getInstance().newEditable("$dayOfMonth / $monthOfYear / $year")
        }, year, month, day)
        dialog.datePicker.minDate = Date().time
        dialog.show()
    }
}
