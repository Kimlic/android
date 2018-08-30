package com.kimlic.recovery

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.SyncService
import com.kimlic.managers.PresentationManager
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_account_recovery.*

class AccountRecoveryActivity : BaseActivity() {

    // Constants

    private val GOOGLE_SIGNE_IN_REQUEST_CODE = 105

    // Variables

    private var recoveryViewModel: RecoveryViewModel? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_recovery)

        recoveryViewModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)
        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GOOGLE_SIGNE_IN_REQUEST_CODE -> if (resultCode != Activity.RESULT_OK) finish()
        }
    }

    // Private

    private fun setupUI() {
        lifecycle.addObserver(recoveryViewModel!!)
        SyncService.signIn(this, GOOGLE_SIGNE_IN_REQUEST_CODE)
        backTv.setOnClickListener { PresentationManager.signUpRecovery(this) }

        verifyBt.setOnClickListener { _ ->
            val mnemonic = phraseEt.text.toString().trim()

            if (!mnemonicValid(mnemonic)) {
                showPopup(title = getString(R.string.error), message = getString(R.string.missing_mnemonic_phrases)); return@setOnClickListener
            }

            GoogleSignIn.getLastSignedInAccount(this)?.let { recoveryUserProfile(mnemonic) }
        }
    }

    private fun recoveryUserProfile(mnemonic: String) {
        recoveryViewModel!!.recoveryProfile(mnemonic,
                onSuccess = { successful() },
                onError = { errorMessage ->
                    errorPopup(errorMessage); return@recoveryProfile
                })
    }

    // Private helpers

    private fun successful() {
        val fragment = RecoverySuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finishAffinity()
                PresentationManager.stage(this@AccountRecoveryActivity)
            }
        })
        fragment.show(supportFragmentManager, RecoverySuccessfulFragment.FRAGMENT_KEY)
    }

    private fun mnemonicValid(mnemonic: String) = mnemonic.split(" ").size == 12

    private fun showProgress() {

    }

    private fun hideProgress() {

    }
}