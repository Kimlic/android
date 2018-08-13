package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.PortraitPhotoFragment
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback

class PortraitActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var model: ProfileViewModel

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
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        initFragment()

        portraitFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                saveUserPhoto(data)
                finish()
            }
        })
        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)
    }

    private fun initFragment() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        portraitFragment = PortraitPhotoFragment.newInstance(bundle)
    }

    private fun saveUserPhoto(data: ByteArray) {
        model.addUserPortraitPhoto(data)
    }
}
