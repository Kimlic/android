package com.kimlic

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.splash.SplashScreenActivity
import com.kimlic.splash.SplashScreenFragment
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.QuorumURL
import org.json.JSONObject

class MainActivity : BaseActivity() {

    // Constants

    val SPLASH_SCREEN_REQUEST_CODE = 124

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        QuorumKimlic.createInstance(null, this)
        val mnemonic = QuorumKimlic.getInstance().mnemonic
        Log.e("MAIN ACTIVITY", "MNEMONIC: $mnemonic")

        setupUI()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (resultCode) {
//            SPLASH_SCREEN_REQUEST_CODE -> {
//                // in case SplashActivity
//            }
//        }
//    }

    private fun setupUI() {
        //if (true) splashScreenFragment()
         PresentationManager.stage(this)
        quorumRequest()

    }

    private fun quorumRequest() {
        val address = QuorumKimlic.getInstance().walletAddress
        val headers = mapOf<String, String>(Pair("account-address", address))
        val addressRequest = KimlicRequest(Request.Method.GET, QuorumURL.config.url, headers, null, Response.Listener {
            Log.e(TAG, "CONFIG: " + it)
            val json = JSONObject(it)
            val responceCode = json.getJSONObject("meta").optString("code").toString()

            if (responceCode.startsWith("2")) {
                val accountStorageAdapterAddress = json.getJSONObject("data").optString("context_contract")
                QuorumKimlic.getInstance().setAccountStorageAdapterAddress(accountStorageAdapterAddress)
            } else {
                // should show error
            }
        }, Response.ErrorListener {
            // should show error
        })
        VolleySingleton.getInstance(this@MainActivity).requestQueue.add(addressRequest)

    }

    private fun splshScreenActivity() {
        val splash = Intent(this, SplashScreenActivity::class.java)
        startActivityForResult(splash, SPLASH_SCREEN_REQUEST_CODE)
    }

    private fun splashScreenFragment() {
        val fragment = SplashScreenFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                if (Prefs.authenticated) {
                    if (Prefs.isPasscodeEnabled) {
                        if (Prefs.isTouchEnabled) {
                            PresentationManager.login(this@MainActivity)
                        } else {
                            PresentationManager.passcodeUnlock(this@MainActivity)
                        }
                    } else
                        PresentationManager.stage(this@MainActivity)
                } else
                    PresentationManager.signupRecovery(this@MainActivity)
            }
        })
        fragment.show(supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)
    }
}
