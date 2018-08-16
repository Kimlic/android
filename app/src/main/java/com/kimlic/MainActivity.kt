package com.kimlic

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.splash.SplashScreenFragment
import com.kimlic.utils.QuorumURL
import org.json.JSONObject

class MainActivity : BaseActivity() {

    // Variables

    private lateinit var splashFragment: SplashScreenFragment
    private lateinit var model: ProfileViewModel


    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    // Private

    private fun setupUI() {
        initFragment()
        splashScreenShow()

        if (Prefs.authenticated) {
            model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
            model.syncProfile(Prefs.currentAccountAddress)
            quorumRequest()
        } else {
            object : CountDownTimer(3000, 3000) {
                override fun onFinish() {
                    splashScreenHide(); PresentationManager.signupRecovery(this@MainActivity)
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()
        }
    }

    private fun quorumRequest() {
        Log.d(TAG, "account address = " + Prefs.currentAccountAddress)
        val user = model.getUser(Prefs.currentAccountAddress)
        // 1. Create Quorum instance with current user
        //val user = KimlicDB.getInstance()!!.userDao().selectLive(id = Prefs.currentId)
        //val user = KimlicDB.getInstance()!!.userDao().selectLive(id = Prefs.currentId)

        val mnemonic = user.mnemonic
        QuorumKimlic.destroyInstance()
        QuorumKimlic.createInstance(mnemonic, this)//Create Quorum instance

        val walletAddress = user.accountAddress

        // 2. Get entry point of the Quorum

        val headers = mapOf(Pair("account-address", walletAddress))

        val addressRequest = object : KimlicRequest(Request.Method.GET, QuorumURL.config.url, headers, null, Response.Listener {
            val responseCode = JSONObject(it).getJSONObject("meta").optString("code").toString()

            if (!responseCode.startsWith("2")) {
                Log.d("TAGSERVER", "in server response in response")
                errorPopup(getString(R.string.server_error)); return@Listener
            }
            Log.d("TAGSERVER", "in server response in response NO PROBLEM")
            val contextContractAddress = JSONObject(it).getJSONObject("data").optString("context_contract")
            QuorumKimlic.getInstance().setKimlicContractsContextAddress(contextContractAddress)

            val accountStorageAdapterAddress = QuorumKimlic.getInstance().accountStorageAdapter
            QuorumKimlic.getInstance().setAccountStorageAdapterAddress(accountStorageAdapterAddress)

            object : CountDownTimer(1500, 1500) {
                override fun onFinish() {
                    continueApp()
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()
        }, Response.ErrorListener {

//                        object : CountDownTimer(1500, 1500) {
//                override fun onFinish() {
//
//                    quorumRequest() }
//
//                override fun onTick(millisUntilFinished: Long) {}
//            }.start()
            Log.d("TAGSERVER", "inResponse error listener  = ${it}")
            errorPopup(getString(R.string.server_error))
        }) {
            init {
                retryPolicy = DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        }

        Handler().postDelayed({ VolleySingleton.getInstance(this@MainActivity).requestQueue.add(addressRequest) }, 1500)
    }

    private fun initFragment() {
        splashFragment = SplashScreenFragment.newInstance()
    }

    private fun splashScreenHide() {
        splashFragment.dismiss()
    }

    private fun splashScreenShow() {
        splashFragment.show(supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)
    }

    private fun continueApp() {
        if (Prefs.isPasscodeEnabled) {
            if (Prefs.isTouchEnabled) {
                PresentationManager.login(this@MainActivity)
            } else PresentationManager.passcodeUnlock(this@MainActivity)
        } else PresentationManager.stage(this@MainActivity)
    }
}