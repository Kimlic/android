package com.kimlic

import android.content.Intent
import android.os.Bundle
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.splash.SplashScreenActivity
import com.kimlic.splash.SplashScreenFragment
import com.kimlic.utils.BaseCallback

class MainActivity : BaseActivity() {

    // Constants

    val SPLASH_SCREEN_REQUEST_CODE = 124

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
       // if (true) splashScreenFragment()
        PresentationManager.stage(this)
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
