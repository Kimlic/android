package com.kimlic.camera_id_verification

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.camera_id_verification.fragments.PortraitPhotoFragment
import com.kimlic.camera_id_verification.fragments.IDBackPhotoFragment
import com.kimlic.camera_id_verification.fragments.IDPhotoFragment

class DocumentVerifyActivity : BaseActivity() {

    // Variables

    private lateinit var documentType: String
    private lateinit var facePhotoFragment: PortraitPhotoFragment
    private lateinit var idPhotoFragment: IDPhotoFragment
    private lateinit var idBackPhotoFragment: IDBackPhotoFragment

    private lateinit var viewModel: DocumentVerifyViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        setupUI()
    }

    // Private

    private fun setupUI() {
        viewModel = ViewModelProviders.of(this).get(DocumentVerifyViewModel::class.java)
        viewModel.setDocumentType("passport")


        initFragmentas()
        //documentType = intent.extras.getString("", "passport")
        showFragment(R.id.container, facePhotoFragment, PortraitPhotoFragment.FRAGMENT_KEY)

    }

    private fun initFragmentas() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)

        facePhotoFragment = PortraitPhotoFragment.newInstance(bundle)
        idPhotoFragment = IDPhotoFragment.newInstance()
        idBackPhotoFragment = IDBackPhotoFragment.newInstance()
    }
}
