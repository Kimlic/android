package com.kimlic

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
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
        Log.e(TAG, "AAAAAAA")
        initFragment()
        Log.e(TAG, "BBBBBBB")
        splashScreenShow()
        Log.e(TAG, "CCCCCCC")
        if (Prefs.authenticated) {
            model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
            Log.e(TAG, "DDDDDDDDD")
            model.syncProfile(Prefs.currentAccountAddress)
            Log.e(TAG, "EEEEEEEEE")
            quorumRequest()
            Log.e(TAG, "FFFFFFFFF")
        } else {
            Log.e(TAG, "GGGGGGGGG")
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

        val headers = mapOf<String, String>(Pair("account-address", walletAddress))
        Log.e("AAAAA", "11111")
        val addressRequest = KimlicRequest(Request.Method.GET, QuorumURL.config.url, headers, null, Response.Listener {
            val responseCode = JSONObject(it).getJSONObject("meta").optString("code").toString()

            if (!responseCode.startsWith("2")) {
                errorPopup(getString(R.string.server_error)); return@Listener
            }
            Log.e("AAAAA", "22222")
            val contextContractAddress = JSONObject(it).getJSONObject("data").optString("context_contract")
            QuorumKimlic.getInstance().setKimlicContractsContextAddress(contextContractAddress)
            Log.e("AAAAA", "33333")
            val accountStorageAdapterAddress = QuorumKimlic.getInstance().accountStorageAdapter
            QuorumKimlic.getInstance().setAccountStorageAdapterAddress(accountStorageAdapterAddress)
            Log.e("AAAAA", "44444")
//            object : CountDownTimer(1500, 1500) {
//                override fun onFinish() {
//                    Log.e("AAAAA", "555555")
//                    continueApp()
//                }
//
//                override fun onTick(millisUntilFinished: Long) {}
//            }.start()
        }, Response.ErrorListener {
            //            object : CountDownTimer(1500, 1500) {
//                override fun onFinish() { quorumRequest() }
//
//                override fun onTick(millisUntilFinished: Long) {}
//            }.start()
//            errorPopup(getString(R.string.server_error))
            Log.e("AAAAA", "6666666")
        })
        VolleySingleton.getInstance(this@MainActivity).requestQueue.add(addressRequest)
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