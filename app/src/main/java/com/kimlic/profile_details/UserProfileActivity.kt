package com.kimlic.profile_details

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.phone.PhoneSuccessfullFragment
import com.kimlic.video_id_verification.fragments.IDPhotoSuccessfullFragment

class UserProfileActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        setupUI()
    }

    // Private

    private fun setupUI() {
        showFragment(R.id.fragment_container, IDPhotoSuccessfullFragment.newInstance(), PhoneSuccessfullFragment.FRAGMENT_KEY)

    }
}
