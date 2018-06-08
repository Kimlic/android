package com.kimlic.auth

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.FingerprintService
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupUI()
    }

    // Private

    private fun setupUI() {

        val fingerprintService = FingerprintService(this, { accept() }, { showToast(it); finish() })

        usePasscodeBt.setOnClickListener {
            PresentationManager.passcodeUnlock(this)
        }
    }

    private fun accept() {
        showToast("Accepted")
        // Next Activity
    }
}
