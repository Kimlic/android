package com.kimlic.camera_id_verification

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.camera_id_verification.fragments.PortraitPhotoFragment
import com.kimlic.camera_id_verification.fragments.IDBackPhotoFragment
import com.kimlic.camera_id_verification.fragments.IDPhotoFragment

class VideoIdActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_id)

       // faceFragment()
       // idPhotoFragment()
        idBackPhotoFragment()
    }

    // Private

    private fun faceFragment() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
        val fragment = PortraitPhotoFragment.newInstance(bundle)
        showFragment(R.id.containerVideo, fragment, PortraitPhotoFragment.FRAGMENT_KEY)
    }

    private fun idPhotoFragment() {
        showFragment(R.id.containerVideo, IDPhotoFragment.newInstance(), IDPhotoFragment.FRAGMENT_KEY)
    }

    private fun idBackPhotoFragment() {
        showFragment(R.id.containerVideo, IDBackPhotoFragment.newInstance(), IDBackPhotoFragment.FRAGMENT_KEY)
    }
}