package com.kimlic.video_id_verification

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.video_id_verification.fragments.PortraitPhotoFragment

class PortraitActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portrait)

        setupUI()
    }

    // Private

    private fun setupUI() {
        initFragments()

        showFragment(R.id.container, portraitFragment, "portrait", "portraitFragment")
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        bundle.putString(AppConstants.filePathRezult.key, AppConstants.userStagePortraitFileName.key)
        portraitFragment = PortraitPhotoFragment.newInstance(bundle)
    }

}
