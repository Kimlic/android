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

    private lateinit var portraitData: ByteArray
    private lateinit var frontData: ByteArray
    private lateinit var backData: ByteArray
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount < 2) finish()
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
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
                saveDocument(documentType, portraitData, frontData, backData)
                successfull()
            }
        })
    }

    private fun initFragments() {
        val portraitBundle = Bundle()
        portraitBundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)

        portraitFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        frontFragment = DocumentFrontFragment.newInstance()
        backFragment = DocumentBackFragment.newInstance()
    }

    private fun successfull() {
        val fragment = VerifySuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
                //PresentationManager.stage(this@DocumentVerifyActivity)
            }
        })

        fragment.show(supportFragmentManager, VerifySuccessfulFragment.FRAGMENT_KEY)
    }

    private fun saveDocument(documentType: String, portraitData: ByteArray, frontData: ByteArray, backData: ByteArray) {
        model.saveDocumentAndPhoto(documentType, portraitData, frontData, backData)
    }
}
