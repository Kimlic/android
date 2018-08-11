package com.kimlic.recovery

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_account_recovery.*

class AccountRecoveryActivity : BaseActivity() {

    // Variables

    private var recoveryViewModel: RecoveryViewModel? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_recovery)

        setupUI()
    }

    // Private

    private fun setupUI() {
        recoveryViewModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)
        backTv.setOnClickListener {
            PresentationManager.signupRecovery(this)
        }

        verifyBt.setOnClickListener {
            val mnemonic = phraseEt.text.toString().trim()

            if (!mnemonicValid(mnemonic)) {
                showPopup(title = "Error", message = "Missing mnemonic phrases"); return@setOnClickListener
            }

            QuorumKimlic.destroyInstance()
            val quorumKimlic = QuorumKimlic.createInstance(mnemonic, this)//Create Quorum instance
            val accountAddress = quorumKimlic.walletAddress

            Log.d("TAGPHRASELIST", "account address = " + accountAddress)

            recoveryViewModel!!.retrivePhoto(accountAddress)

            KimlicDB.getInstance()!!.close()
            recoveryViewModel!!.retriveDatabase(accountAddress = accountAddress, onSuccess = {
                Prefs.authenticated = true
                Prefs.currentAccountAddress = accountAddress
                KimlicDB.getInstance()
                Log.d(TAG, "Database restored successfully")
                successfull()
            }, onError = {
                showPopup("Error", "Recovery Error")
                return@retriveDatabase
            })

        }
    }

    private fun getPhraseList(): List<String> {
        val phraseText = phraseEt.text.toString()
        val phraseList: List<String> = phraseText.trim().split(" ")
        return phraseList
    }

    private fun successfull() {
        val fragment = RecoverySuccesfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finishAffinity()
                PresentationManager.stage(this@AccountRecoveryActivity)
            }
        })
        fragment.show(supportFragmentManager, RecoverySuccesfullFragment.FRAGMENT_KEY)
    }

    private fun mnemonicValid(mnemonic: String): Boolean {
        return mnemonic.split(" ").size == 12
    }
}
