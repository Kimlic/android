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
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_verify_details.*
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
            "send" -> setupSend()
            "preview" -> setupPreview(currentDocument)
        }
        backBt.setOnClickListener { finish() }
    }

    private fun initExtraVariables() {
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        action = intent.extras.getString("action", "preview")
        country = intent.extras.getString("country", "")
        url = intent.extras.getString("path", "")

        currentDocument = model.userDocument(documentType)
        user = model.user()
        photosMap = model.userDocumentPhotos(documentType = documentType).map { it.type to it.file }.toMap()
    }

    private fun setupPreview(document: Document) {
        when (document.state) {
            DocState.CREATED.state, DocState.VERIFIED.state -> {
                countryEt.text = Editable.Factory.getInstance().newEditable(document.country)
                countryTil.visibility = View.VISIBLE
                disableEditing(textFields)
                addBt.visibility = View.GONE
            }
            else -> {
                if (model.hasDocumentInProgress()) {
                    disableEditing(textFields.subList(0, 2))
                }

                countryTil.visibility = View.GONE
                addBt.text = getString(R.string.add_details)

                expireDateEt.setOnClickListener { datePicker() }
                addBt.setOnClickListener {
                    if (validFields()) {
                        updateDocument()
                        updateUserName(firstNameEt.text.toString(), lastNameEt.text.toString())
                        finish()
                    }
                }
            }
        }
    }

    private fun setupSend() {
        countryTil.visibility = View.VISIBLE
        countryEt.text = Editable.Factory.getInstance().newEditable(country)

        if (model.hasDocumentInProgress()) {
            disableEditing(textFields.subList(0, 2))
        }

        expireDateEt.setOnClickListener { datePicker() }
        addBt.text = getString(R.string.verify_document)
        addBt.setOnClickListener {
            if (validFields()) {
                addBt.isClickable = false
                updateUserName(firstNameEt.text.toString(), lastNameEt.text.toString())
                updateDocument()
                sendDocument()
            }
        }
    }

    private fun sendDocument() {
        showProgress()
        model.senDoc(docType = documentType, country = country, url = url,
                onSuccess = {
                    hideProgress()
                    currentDocument.state = DocState.CREATED.state
                    model.updateDocument(currentDocument) //finish ()
                    showPopup("Success!", "Document sent!", action = { finish() })
                },
                onError = {
                    hideProgress()
                    addBt.isClickable = true
                    showPopup("Error", message = "Unable to proceed!", action = {})
                })
    }

    // Updates

    private fun updateUserName(firstName: String, lastName: String) = model.updateUserName(firstName, lastName)

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

    // exit from activity on success adding???
    private fun showPopup(title: String, message: String, action: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> action(); dialog.dismiss() }
                .setCancelable(true)
                .setOnDismissListener { action() }
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

    private fun disableEditing(views: List<EditText>) =
            views.forEach { it.isClickable = false; it.isFocusableInTouchMode = false; it.isFocusable = false; it.isCursorVisible = false }
}