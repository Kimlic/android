package com.kimlic.camera_id_verification

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.camera_id_verification.fragments.IDBackPhotoFragment
import com.kimlic.camera_id_verification.fragments.IDPhotoFragment
import com.kimlic.camera_id_verification.fragments.PortraitPhotoFragment
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback

class PassportVerifyActivity : BaseActivity() {

    // Variables

    private lateinit var portraitPhotoFragment: PortraitPhotoFragment
    private lateinit var idPhotoFragment: IDPhotoFragment
    private lateinit var idBackPhotoFragment: IDBackPhotoFragment

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport_verify)

        setupUI()
    }

    // Private

    private fun setupUI() {
        initFragments()

        showFragment(R.id.container, portraitPhotoFragment, PortraitPhotoFragment.FRAGMENT_KEY)

        portraitPhotoFragment.setCallback(object : BaseCallback {
            override fun callback() {
                showFragment(R.id.container, idPhotoFragment, IDPhotoFragment.FRAGMENT_KEY)
            }
        })

        idPhotoFragment.setCallback(object : BaseCallback {
            override fun callback() {
                showFragment(R.id.container, idBackPhotoFragment, IDBackPhotoFragment.FRAGMENT_KEY)
            }
        })

        idBackPhotoFragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
            }
        })

    }

    private fun initFragments() {
        val portraitBundle = Bundle()
        val frontBundle = Bundle()
        val backBundle = Bundle()

        portraitBundle.putString(AppConstants.filePathRezult.key, AppConstants.userPassportPortraitFileName.key)
        frontBundle.putString(AppConstants.filePathRezult.key, AppConstants.userPassportFrontSideFileName.key)
        backBundle.putString(AppConstants.filePathRezult.key, AppConstants.userPassportBackSideFileName.key)

        portraitPhotoFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        idPhotoFragment = IDPhotoFragment.newInstance(frontBundle)
        idBackPhotoFragment = IDBackPhotoFragment.newInstance(backBundle)
    }

}
