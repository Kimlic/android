package com.kimlic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kimlic.db.KimlicDB
import com.kimlic.db.User
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.terms.TermsActivity
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.activity_signup_recovery.*

class SignupRecoveryActivity : BaseActivity() {

    // Constants

    private val TERMS_ACCEPT_RECOVERY_REQUEST_CODE = 101
    private val TERMS_ACCEPT_CREATE_REQUEST_CODE = 102

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_recovery)

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
            PresentationManager.tutorials(this)
            //termsToAccept(TERMS_ACCEPT_CREATE_REQUEST_CODE)
            initNewUserRegistaration()

//            QuorumKimlic.createInstance(null, this)
//            val mnemonic = QuorumKimlic.getInstance().mnemonic
//            Log.d("TAGSIGNUP", "mnemonic - " + mnemonic)
//            KimlicDB.getInstance()!!.userDao().findById(Prefs.userId)
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

    private fun initNewUserRegistaration() {
        QuorumKimlic.createInstance(null, this)
        val mnemonic = QuorumKimlic.getInstance().mnemonic
        val walletAddress = QuorumKimlic.getInstance().walletAddress
        val user = User(id = Prefs.userId, mnemonic = mnemonic, blockchainAddress = walletAddress)
        KimlicDB.getInstance()!!.userDao().insert(user)
        Log.d("TAGMNEMONIC", "mnemonic - " + mnemonic)
        Log.d("TAGMNEMONIC", "walletAddress - " + walletAddress)


    }
}
