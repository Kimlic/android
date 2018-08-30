package com.kimlic.recovery

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.terms.TermsActivity
import kotlinx.android.synthetic.main.activity_signup_recovery.*

class SignUpRecoveryActivity : BaseActivity() {

    // Variables

    private lateinit var recoveryModel: RecoveryViewModel

    // Constants

    private val TERMS_ACCEPT_RECOVERY_REQUEST_CODE = 101
    private val TERMS_ACCEPT_CREATE_REQUEST_CODE = 102

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
            termsToAccept(TERMS_ACCEPT_RECOVERY_REQUEST_CODE)
        }
    }

    private fun termsToAccept(requestCode: Int) {
        val intent = Intent(this, TermsActivity::class.java)
        intent.putExtra("action", "accept")
        intent.putExtra("content", "terms")
        startActivityForResult(intent, requestCode)
    }
}