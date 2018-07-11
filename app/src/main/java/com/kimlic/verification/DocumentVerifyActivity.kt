package com.kimlic.verification

import android.os.Bundle
import android.util.Log
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.UserPhotos
import com.kimlic.verification.fragments.DocumentBackFragment
import com.kimlic.verification.fragments.DocumentFrontFragment
import com.kimlic.verification.fragments.PortraitPhotoFragment

class DocumentVerifyActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var frontFragment: DocumentFrontFragment
    private lateinit var backFragment: DocumentBackFragment

    private lateinit var portraitFilePath: String
    private lateinit var documentFrontSideFilePath: String
    private lateinit var documentBackSideFilePath: String
    private lateinit var documentType: String
    private val defaultPath = UserPhotos.default.fileName

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.getBackStackEntryCount() < 2) {
            finish(); deleteFotos(documentType)
        }
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        initFragmentas()

        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)

        portraitFragment.setCallback(object : BaseCallback {
            override fun callback() {
                showFragment(R.id.container, frontFragment, DocumentFrontFragment.FRAGMENT_KEY)
            }
        })
        frontFragment.setCallback(object : BaseCallback {
            override fun callback() {
                showFragment(R.id.container, backFragment, DocumentBackFragment.FRAGMENT_KEY)
            }
        })
        backFragment.setCallback(object : BaseCallback {
            override fun callback() {
                Prefs.documentToverify = documentType
                successfull()
            }
        })

    }

    private fun initFragmentas() {
        portraitFilePath = intent.extras.getString(UserPhotos.portraitFilePath.fileName, defaultPath)
        documentFrontSideFilePath = intent.extras.getString(UserPhotos.frontFilePath.fileName, defaultPath)
        documentBackSideFilePath = intent.extras.getString(UserPhotos.backFilePath.fileName, defaultPath)

        val portraitBundle = Bundle()
        val frontBundle = Bundle()
        val backBundle = Bundle()

        portraitBundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        portraitBundle.putString(AppConstants.filePathRezult.key, portraitFilePath)
        frontBundle.putString(AppConstants.filePathRezult.key, documentFrontSideFilePath)
        backBundle.putString(AppConstants.filePathRezult.key, documentBackSideFilePath)

        portraitFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        frontFragment = DocumentFrontFragment.newInstance(frontBundle)
        backFragment = DocumentBackFragment.newInstance(backBundle)
    }

    private fun successfull() {
        val fragment = VerifySuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@DocumentVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, VerifySuccessfullFragment.FRAGMENT_KEY)
    }

    private fun deleteFotos(targetFile: String) {
        // TODO delete files?
    }
}
