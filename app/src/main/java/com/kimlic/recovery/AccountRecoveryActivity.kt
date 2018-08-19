package com.kimlic.recovery

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_account_recovery.*

class AccountRecoveryActivity : BaseActivity() {

    // Constants

    private val GOOGLE_SIGNE_IN_REQUEST_CODE = 105

    // Variables

    private var recoveryViewModel: RecoveryViewModel? = null
    //private lateinit var syncService: SyncService

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_recovery)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GOOGLE_SIGNE_IN_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    Prefs.isUserGoogleSigned = false; finish()
                }
                Prefs.isUserGoogleSigned = true

            }
        }
    }

    // Private

    private fun setupUI() {
        recoveryViewModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)

        backTv.setOnClickListener { PresentationManager.signUpRecovery(this) }
        verifyBt.setOnClickListener {
            val mnemonic = phraseEt.text.toString().trim()

            if (!mnemonicValid(mnemonic)) {
                showPopup(title = getString(R.string.error), message = getString(R.string.missing_mnemonic_phrases)); return@setOnClickListener
            }

            QuorumKimlic.destroyInstance()
            val quorumKimlic = QuorumKimlic.createInstance(mnemonic, this)
            val accountAddress = quorumKimlic.walletAddress

            if (GoogleSignIn.getLastSignedInAccount(this) != null)
                recoveryProfile(accountAddress)
            else SyncService.signIn(this, GOOGLE_SIGNE_IN_REQUEST_CODE)

        }
    }

    private fun recoveryProfile(accountAddress: String) {
        KimlicDB.getInstance()!!.close()
        recoveryViewModel!!.retrieveDatabase(accountAddress,
                onSuccess = {

                    Log.d(TAG, "Database restored successfully")
                    recoveryViewModel!!.retrievePhoto(accountAddress,
                            onSuccess = {
                                Prefs.authenticated = true
                                Prefs.currentAccountAddress = accountAddress
                                KimlicDB.getInstance()
                                Log.d(TAG, "Database restored successfully")
                                successful()
                            },
                            onError = {
                                errorPopup("Photos recovering Error!!!")
                            })

                    Prefs.authenticated = true
                    Prefs.currentAccountAddress = accountAddress
                    KimlicDB.getInstance()
                    Log.d(TAG, "Database restored successfully")
                    successful()
                },
                onError = {
                    errorPopup("Recovery Error database not found!!!")
                    return@retrieveDatabase
                })
    }

    private fun getPhraseList(): List<String> {
        val phraseText = phraseEt.text.toString()
        return phraseText.trim().split(" ")
    }

    private fun successful() {
        val fragment = RecoverySuccesfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finishAffinity()
                PresentationManager.stage(this@AccountRecoveryActivity)
            }
        })
        fragment.show(supportFragmentManager, RecoverySuccesfulFragment.FRAGMENT_KEY)
    }

    private fun mnemonicValid(mnemonic: String): Boolean {
        return mnemonic.split(" ").size == 12
    }
}