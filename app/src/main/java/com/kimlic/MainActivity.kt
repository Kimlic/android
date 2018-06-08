package com.kimlic

import android.content.Intent
import android.os.Bundle
import com.kimlic.managers.PresentationManager
import com.kimlic.splash_screen.SplashScreenActivity
import com.kimlic.splash_screen.SplashScreenFragment
import com.kimlic.utils.BaseCallback

class MainActivity : BaseActivity() {

    // Constants

    val SPLASH_SCREEN_REQUEST_CODE = 124

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            SPLASH_SCREEN_REQUEST_CODE -> {
                // check if user is authentocated
                PresentationManager.signupRecovery(this)

            }
        }
    }

    private fun setupUI() {
        if (true) splshScreenFragment()
        //PresentationManager.documentChooseVerify(this)
    }

    private fun splshScreenActivity() {
        val splash = Intent(this, SplashScreenActivity::class.java)
        startActivityForResult(splash, SPLASH_SCREEN_REQUEST_CODE)
    }

    private fun splshScreenFragment() {
        val fragment = SplashScreenFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                //PresentationManager.stage(this@MainActivity)
                PresentationManager.signupRecovery(this@MainActivity)
            }
        })
        fragment.show(supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)
    }
}
