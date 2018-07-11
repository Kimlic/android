package com.kimlic.verification

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.verification.fragments.PortraitPhotoFragment
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.UserPhotos

class PortraitActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portrait)

        setupUI()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        initFragments()

        portraitFragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
            }
        })
        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        bundle.putString(AppConstants.filePathRezult.key, UserPhotos.portraitFilePath.fileName)
        portraitFragment = PortraitPhotoFragment.newInstance(bundle)
    }
}
