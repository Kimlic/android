package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.Photo
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.mappers.FileNameTxtBase64ToBitmap
import kotlinx.android.synthetic.main.activity_verify_details.*

class DocumentDetails : BaseActivity() {

    // Variables

    private lateinit var timer: CountDownTimer
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null

    private lateinit var documentType: String
    private lateinit var model: ProfileViewModel
    private lateinit var photoList: List<Photo>

    private lateinit var photoMap: Map<String, String>
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
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        target = intent.extras.getString("target", "preview")

        currentDocument = model.getUserDocument(documentType = documentType)
        photoList = model.getUserDocumentPhotos(documentType = documentType)
        photoMap = photoList.map { it.type to it.file }.toMap()

        fillData(photos = photoMap, document = currentDocument)

        when (target) {
            "send" -> {
                country = intent.extras.getString("country", "")
                url = intent.extras.getString("url", "")
                addBt.text = getString(R.string.verify_document)
                addBt.setOnClickListener {
                    if (validFields()) {
                        addBt.isClickable = false
                        showProgress()
                        currentDocument.number = numberEt.text.toString()
                        currentDocument.expireDate = expireDateEt.text.toString()
                        currentDocument.country = countryEt.text.toString()
                        model.updateDocument(currentDocument)

                        model.senDoc(docType = documentType, country = country, url = url,
                                onSuccess = {
                                    showPopup("Success", "document sended!")
//                                currentDocument.state = "pending"
//                                model.updateDocument(currentDocument)
                                    //finish()
                                },
                                onError = {
                                    hideProgress()
                                    addBt.isClickable = true
                                    showPopup("Error", message = "Unable to proceed!")
                                })
                    }
                }
            }
            "preview" -> {
                addBt.text = getString(R.string.add_details)
                addBt.setOnClickListener {
                    if (validFields()) {
                        currentDocument.number = numberEt.text.toString()
                        currentDocument.expireDate = expireDateEt.text.toString()
                        currentDocument.country = countryEt.text.toString()
                        model.updateDocument(currentDocument)
                        finish()
                    }
                }
            }
        }
        backBt.setOnClickListener { finish() }
    }

    private fun fillData(photos: Map<String, String>, document: Document) {
        frontIv.setImageBitmap(croped(photos.get("front")!!))
        backIv.setImageBitmap(croped(photos.get("back")!!))

        numberEt.text = Editable.Factory.getInstance().newEditable(document.number)
        expireDateEt.text = Editable.Factory.getInstance().newEditable(document.expireDate)
        countryEt.text = Editable.Factory.getInstance().newEditable(document.country)

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
        val docError = if (numberEt.text.length < 3) { numberEt.error = error; false } else { numberEt.error= null; true }
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
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer.let { it?.cancel() }
    }
}
