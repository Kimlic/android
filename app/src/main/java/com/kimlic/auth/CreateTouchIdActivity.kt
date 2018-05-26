package com.kimlic.auth

import android.os.Bundle
import com.kimlic.R
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_create_touch_id.*

class CreateTouchIdActivity : AuthActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        setupCallback()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_touch_id)

        setupUI()
    }

    // Private
    private fun setupUI() {
        btSkip.setOnClickListener {
            Prefs.useFingerprint = false
        }
    }

    private fun setupCallback() {
        setAuthCallback(object : AuthCallback {
            override fun onSuccses() {
                Prefs.useFingerprint = true
                showToast("Create and use touch ID accepted")
            }
        })
    }
}
