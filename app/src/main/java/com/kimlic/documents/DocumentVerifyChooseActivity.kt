package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.SelectCountryFragment
import com.kimlic.documents.fragments.SelectDocumentFragment
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants

class DocumentVerifyChooseActivity : BaseActivity() {

    // Variables

    private var country = ""
    private var documentType = ""
    private lateinit var model: ProfileViewModel

    private var countryFragment: SelectCountryFragment? = null
    private var documentFragment: SelectDocumentFragment? = null

    // Life

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
                PresentationManager.verifyDocument(this@DocumentVerifyChooseActivity, documentType, country)
            }
        })
    }
}