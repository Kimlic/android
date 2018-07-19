package com.kimlic

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.db.KimlicDB
import com.kimlic.db.User
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.splash.SplashScreenActivity
import com.kimlic.splash.SplashScreenFragment
import com.kimlic.utils.QuorumURL
import org.json.JSONObject

class MainActivity : BaseActivity() {

    // Constants

    val SPLASH_SCREEN_REQUEST_CODE = 124

    // Variables

    private lateinit var splashFragment: SplashScreenFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
    }

    // Private

    private fun setupUI() {
        initFragment()

        splashScreenShow()

        if (Prefs.authenticated) { //
//            quorumRequest() // To chek and setup smartcontract address and create QuorumKimlic
            PresentationManager.stage(this)
        } else {
            object : CountDownTimer(3000, 3000) {
                override fun onFinish() {
                    splashScreenHide(); PresentationManager.signupRecovery(this@MainActivity)
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()
        }

        //PresentationManager.stage(this)
        //quorumRequest()
        //errorPopup()


//        if (Prefs.authenticated) {
//            // use account address and get smartcontract address
//            // then use quorum requestr
//
//            var address: String
//
//            KimlicDB.getInstance()!!.userDao().findById(Prefs.userId).observe(this, object : Observer<User> {
//                override fun onChanged(user: User?) {
//                    if (user != null) address = user.blockchainAddress else address = ""
//                }
//            })
//        }


    }


    // For authenticated user
    private fun quorumRequest() {
        QuorumKimlic.createInstance(null, this) // Create instrance

        var address: String// = QuorumKimlic.createInstance(null, this).walletAddress // Get from db

        val user = KimlicDB.getInstance()!!.userDao().findById(Prefs.userId)

        address = user.blockchainAddress
//        KimlicDB.getInstance()!!.userDao().findById(Prefs.userId).observe(this, object : Observer<User> {
//            override fun onChanged(user: User?) {
//                if (user != null) address = user.blockchainAddress else address = ""
//            }
//        })

        Log.d("WALLETADDRES", address)



    }

    private fun initFragment() {
        splashFragment = SplashScreenFragment.newInstance()
    }

    private fun splashScreenActivity() {
        val splash = Intent(this, SplashScreenActivity::class.java)
        startActivityForResult(splash, SPLASH_SCREEN_REQUEST_CODE)
    }

    private fun splashScreenHide() {
        splashFragment.dismiss()
    }

    private fun splashScreenShow() {
        splashFragment.show(supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)
    }

    private fun splashScreenFragment() {

//        val fragment = SplashScreenFragment.newInstance()
//        fragment.setCallback(object : BaseCallback {
//            override fun callback() {
//                if (Prefs.authenticated) {
//                    if (Prefs.isPasscodeEnabled) {
//                        if (Prefs.isTouchEnabled) {
//                            PresentationManager.login(this@MainActivity)
//                        } else {
//                            PresentationManager.passcodeUnlock(this@MainActivity)
//                        }
//                    } else
//                        PresentationManager.stage(this@MainActivity)
//                } else
//                    PresentationManager.signupRecovery(this@MainActivity)
//            }
//        })
//        fragment.show(supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)
    }

}
