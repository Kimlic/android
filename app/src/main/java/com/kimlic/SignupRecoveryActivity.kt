package com.kimlic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.db.entity.User
import com.kimlic.db.SyncServise
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.terms.TermsActivity
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_signup_recovery.*
import org.json.JSONObject

class SignupRecoveryActivity : BaseActivity() {

    // Variables

    // Constants

    private val TERMS_ACCEPT_RECOVERY_REQUEST_CODE = 101
    private val TERMS_ACCEPT_CREATE_REQUEST_CODE = 102
    private val GOOGLE_SIGNE_IN_REQUEST_CODE = 103

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
            GOOGLE_SIGNE_IN_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    Prefs.isUserGoogleSigned = false; return
                }
                Prefs.isUserGoogleSigned = true
            }
        }
    }

    // Private

    private fun setupUI() {
        SyncServise.signIn(this, GOOGLE_SIGNE_IN_REQUEST_CODE)

        createBt.setOnClickListener {
            initNewUserRegistaration()
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
        // 1. Create Quorum instance locally - mnemonic and address
        QuorumKimlic.destroyInstance()
        QuorumKimlic.createInstance(null, this) // moved to quorum request
        val mnemonic = QuorumKimlic.getInstance().mnemonic
        val walletAddress = QuorumKimlic.getInstance().walletAddress

        // Init new user

        val user = User(Prefs.currentId, mnemonic = mnemonic, accountAddress = walletAddress)
        Prefs.currentAccountAddress = walletAddress
        model.insertUser(user)

        // 2. Get entry point of the Quorum
        val headers = mapOf<String, String>(Pair("account-address", walletAddress))

        val addressRequest = KimlicRequest(method = Request.Method.GET, url = QuorumURL.config.url, requestHeaders = headers, requestParams = null, onSuccess = Response.Listener {
            val json = JSONObject(it)
            val responceCode = json.getJSONObject("meta").optString("code").toString()

            if (!responceCode.startsWith("2")) {
                errorPopup(getString(R.string.server_error))
                return@Listener
            }
            // 3. Get context contract address
            val contextContractAddress = json.getJSONObject("data").optString("context_contract")
            QuorumKimlic.getInstance().setKimlicContractsContextAddress(contextContractAddress)
            // 3. Set account storage address
            val accountStorageAdapterAddress = QuorumKimlic.getInstance().accountStorageAdapter
            QuorumKimlic.getInstance().setAccountStorageAdapterAddress(accountStorageAdapterAddress)
            PresentationManager.tutorials(this)
        }, onError = Response.ErrorListener {
            errorPopup(getString(R.string.server_error))
        })
        VolleySingleton.getInstance(this@SignupRecoveryActivity).requestQueue.add(addressRequest)

    }
}
