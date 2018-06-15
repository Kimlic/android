package com.kimlic.splash

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R

class SplashScreenActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_splash_screen)

        setupUI()
    }

    // Private

    private fun setupUI() {
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//
//        val animation = AlphaAnimation(ANIM_FROM, ANIM_TO)
//        animation.duration = DURATION
//        animation.repeatCount = Animation.INFINITE
//        ivLogo.startAnimation(animation)
//
//        object : CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
//            override fun onFinish() {
//                showToast("Go to the next activity")
//
//                if (!Prefs.isTutorialShown) PresentationManager.tutorials(this@SplashScreenActivity)
//                PresentationManager.passcode(this@SplashScreenActivity)
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                pbProgress.progress++
//            }
//        }.start()
//    }
    }
}
