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

    // Variables

    private var addressAccepted: Boolean = false

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val quoroomKimlick = QuorumKimlic.getInstance()
        val address = quoroomKimlick.address

        val headers = emptyMap<String, String>().toMutableMap(); headers.put("account-address", address)

        val addressRequest = KimlicRequest(Request.Method.GET, QuorumURL.config.url,
                Response.Listener<String> {
                    val responceCode = JSONObject(it).getJSONObject("meta").optString("code").toString()

                    if (responceCode.startsWith("2")) {
                        addressAccepted = true
                        val context_contract = JSONObject(it).getJSONObject("data").optString("context_contract")
                        //QuorumKimlic.setContextContract(context_contract)
                        Log.d("TAG", "context_contract = " + context_contract.toString())
                    }

                },
                Response.ErrorListener {
//                    object : CountDownTimer(1000, 1000) {
//                        override fun onTick(millisUntilFinished: Long) {}
//                        override fun onFinish() {
//                            quorumRequest()
//                        }
//                    }.start()

                    Log.d("TAGKIMLICE", "Error" + it.networkResponse.statusCode)
                })

        addressRequest.requestHeaders = headers

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
