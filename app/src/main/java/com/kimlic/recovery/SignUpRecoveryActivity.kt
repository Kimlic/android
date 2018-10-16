package com.kimlic.recovery

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_signup_recovery.*

class SignUpRecoveryActivity : BaseActivity() {

    // Constants

    companion object {
        private const val TERMS_ACCEPT_RECOVERY_REQUEST_CODE = 101
        private const val TERMS_ACCEPT_CREATE_REQUEST_CODE = 102
    }

    // Variables

    private lateinit var recoveryModel: RecoveryViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_recovery)

        recoveryModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)
        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TERMS_ACCEPT_RECOVERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Prefs.termsAccepted = true
                    PresentationManager.recovery(this)
                }
            }
            TERMS_ACCEPT_CREATE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Prefs.termsAccepted = true
                    PresentationManager.recovery(this)
                }
            }
        }
    }

    // Private

    private fun setupUI() {
        createBt.setOnClickListener {
            recoveryModel.initNewUserRegistration(
                    onSuccess = { PresentationManager.tutorials(this) },
                    onError = { errorPopup(getString(R.string.server_error)) }
            )
        }
        recoverBt.setOnClickListener {
            PresentationManager.termsAccept(this, TERMS_ACCEPT_RECOVERY_REQUEST_CODE)
        }
    }
}