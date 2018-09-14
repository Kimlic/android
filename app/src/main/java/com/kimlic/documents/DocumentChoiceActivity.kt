package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.SelectCountryFragment
import com.kimlic.documents.fragments.SelectDocumentFragment
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants

class DocumentChoiceActivity : BaseActivity() {

    // Constants

    companion object {
        private const val REQUEST_CODE = 777
    }

    // Variables

    private var country = ""
    private var documentType = ""
    private lateinit var model: ProfileViewModel

    private var countryFragment: SelectCountryFragment? = null
    private var documentFragment: SelectDocumentFragment? = null

    // Life

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE -> {
                setResult(REQUEST_CODE); finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_choose_document_)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        countryFragment = SelectCountryFragment.getInstance()
        countryFragment!!.setCallback(object : DocumentCallback {
            override fun callback(bundle: Bundle) {
                country = bundle.getString(AppConstants.COUNTRY.key)
                documentFragment!!.show(supportFragmentManager, SelectDocumentFragment.FRAGMENT_KEY)
            }
        })

        countryFragment!!.show(supportFragmentManager, SelectCountryFragment.FRAGMENT_KEY)

        documentFragment = SelectDocumentFragment.getInstance()
        documentFragment!!.setCallback(object : DocumentCallback {

            override fun callback(bundle: Bundle) {
                documentType = bundle.getString(AppConstants.DOCUMENT_TYPE.key)
                //PresentationManager.verifyDocument(this@DocumentChoiceActivity, documentType, country)
                startForResult(documentType, country)
            }
        })
    }

    private fun startForResult(documentType: String, country: String) {
        val intent = Intent(this, DocumentVerifyActivity::class.java)
        intent.putExtra(AppConstants.DOCUMENT_TYPE.key, documentType)
        intent.putExtra(AppConstants.COUNTRY.key, country)

        startActivityForResult(intent, REQUEST_CODE)
    }
}