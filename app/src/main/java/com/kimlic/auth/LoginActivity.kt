package com.kimlic.auth

import android.os.Bundle
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AuthActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        setupCallback()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupUI()
    }

    // Private

    private fun setupUI() {
        usePasscodeBt.setOnClickListener {

        }
    }

    private fun setupCallback() {
        setAuthCallback(object : AuthCallback {
            override fun onSuccses() {
                showToast("LoginActivity touch ID approoved. Go to the next Activity")
            }
        })
    }
}
