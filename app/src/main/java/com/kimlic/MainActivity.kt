package com.kimlic

import android.content.Intent
import android.os.Bundle
import com.kimlic.managers.PresentationManager

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PresentationManager.splash(this@MainActivity)
        //startActivity(Intent(this, LoginActivity::class.java))
        //startActivity(Intent(this, CreateTouchIdActivity::class.java))
        //startActivity(Intent(applicationContext, SplashScreenActivity::class.java))
        //startActivity(Intent(applicationContext, PasscodeActivity::class.java))
        //startActivity(Intent(applicationContext, TutorialActivity::class.java))
    }
}
