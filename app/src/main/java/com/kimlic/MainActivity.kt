package com.kimlic

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.KimlicRequest
import com.kimlic.API.SyncObject
import com.kimlic.API.VolleySingleton
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.splash.SplashScreenFragment
import com.kimlic.utils.QuorumURL
import org.json.JSONObject

class MainActivity : BaseActivity() {

    // Variables

    private lateinit var splashFragment: SplashScreenFragment
    // private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    // Private

    private fun setupUI() {
        initFragment()
        splashScreenShow()

//        Prefs.currentAccountAddress = "sdcadvaffvervevcxaervrvrisjdbfiedejbvkekrjbvkjbvb"
//        val user = User(id = Prefs.currentId, accountAddress = "sdcadvaffvervevcxaervrvrisjdbfiedejbvkekrjbvkjbvb", mnemonic = "efvcvefvcefvcervrvrwgwrgvefveveverc")
//        KimlicDB.getInstance()!!.userDao().insert(user)
//        PresentationManager.stage(this)

        if (Prefs.authenticated) {
            Handler().post({ profileSynckRequest(Prefs.currentAccountAddress) })
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
        val user = model.getUser(Prefs.currentAccountAddress)
        // 1. Create Quorum instance with current user
        //val user = KimlicDB.getInstance()!!.userDao().select(id = Prefs.currentId)
        //val user = KimlicDB.getInstance()!!.userDao().select(id = Prefs.currentId)

        val mnemonic = user.mnemonic
        QuorumKimlic.destroyInstance()
        QuorumKimlic.createInstance(mnemonic, this)//Create Quorum instance

        val walletAddress = user.accountAddress

        // 2. Get entry point of the Quorum

        val headers = mapOf<String, String>(Pair("account-address", walletAddress))

        val addressRequest = KimlicRequest(Request.Method.GET, QuorumURL.config.url, headers, null, Response.Listener {
            val responceCode = JSONObject(it).getJSONObject("meta").optString("code").toString()

            if (!responceCode.startsWith("2")) {
                errorPopup(getString(R.string.server_error)); return@Listener
            }

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
            //            object : CountDownTimer(1500, 1500) {
//                override fun onFinish() { quorumRequest() }
//
//                override fun onTick(millisUntilFinished: Long) {}
//            }.start()
            errorPopup(getString(R.string.server_error))
        })
        VolleySingleton.getInstance(this@MainActivity).requestQueue.add(addressRequest)
    }

    private fun profileSynckRequest(accountAddress: String) {
        val headers = mapOf(Pair("account-address", accountAddress))

        val syncRequest = KimlicRequest(Request.Method.GET, QuorumURL.profileSync.url, headers, null,
                Response.Listener {
                    val responceCode = JSONObject(it).getJSONObject("meta").optString("code").toString()

                    if (!responceCode.startsWith("2")) return@Listener

                    val jsonToParce = JSONObject(it).getJSONObject("data").getJSONArray("data_fields").toString()
                    val type = object : TypeToken<List<SyncObject>>() {}.type
                    val approvedObjects: List<SyncObject> = Gson().fromJson(jsonToParce, type)
                    val approved = approvedObjects.map { it.name }

                    if (!approved.contains("phone")) model.deleteUserContact(Prefs.currentAccountAddress, "phone")
                    if (!approved.contains("email")) model.deleteUserContact(Prefs.currentAccountAddress, "email")
                },
                Response.ErrorListener {})

        VolleySingleton.getInstance(this@MainActivity).requestQueue.add(syncRequest)
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