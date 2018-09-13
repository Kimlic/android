package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.DocumentBackFragment
import com.kimlic.documents.fragments.DocumentFrontFragment
import com.kimlic.documents.fragments.PortraitPhotoFragment
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.PhotoCallback

class DocumentVerifyActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var frontFragment: DocumentFrontFragment
    private lateinit var backFragment: DocumentBackFragment

    private lateinit var documentType: String
    private lateinit var country: String

    private lateinit var portraitData: ByteArray
    private lateinit var frontData: ByteArray
    private lateinit var backData: ByteArray
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount < 2) finish()
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        documentType = intent.extras.getString(AppConstants.DOCUMENT_TYPE.key, "")
        country = intent.extras.getString(AppConstants.COUNTRY.key, "")

        initFragments()

        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)

        portraitFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                portraitData = data
                showFragment(R.id.container, frontFragment, DocumentFrontFragment.FRAGMENT_KEY)
            }
        })
        frontFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                frontData = data
                showFragment(R.id.container, backFragment, DocumentBackFragment.FRAGMENT_KEY)
            }
        })
        backFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                backData = data
                saveDocument(documentType, country, portraitData, frontData, backData)
                successful()
            }
        })
    }

    private fun initFragments() {
        val portraitBundle = Bundle()
        portraitBundle.putInt(AppConstants.CAMERA_TYPE.key, AppConstants.CAMERA_FRONT.intKey)
        portraitBundle.putString("action", AppConstants.PORTRAIT_DOCUMENT.key)

        portraitFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        frontFragment = DocumentFrontFragment.newInstance()
        backFragment = DocumentBackFragment.newInstance()
    }

    private fun successful() {
        val fragment = DocumentSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
            }
        })

        fragment.show(supportFragmentManager, DocumentSuccessfulFragment.FRAGMENT_KEY)
    }

    private fun saveDocument(documentType: String, country: String, portraitData: ByteArray, frontData: ByteArray, backData: ByteArray) {
        model.saveDocumentAndPhoto(documentType, country, portraitData, frontData, backData)
    }
}