package com.kimlic.splash_screen

import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : BaseActivity() {

    // Constants

    private val ANIM_FROM = 1F
    private val ANIM_TO = 0.8F
    private val DURATION = 1000L
    private val COUNT_DOWN_INTERVAL = 17L
    private val MILLIS_IN_FUTURE = 3000L

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setupUI()
    }

    // Private

    private fun setupUI() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        val animation = AlphaAnimation(ANIM_FROM, ANIM_TO)
        animation.duration = DURATION
        animation.repeatCount = Animation.INFINITE
        ivLogo.startAnimation(animation)

        object : CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            override fun onFinish() {
                showToast("Go to the next activity")

                if (!Prefs.isTutorialShown) PresentationManager.tutorials(this@SplashScreenActivity)
            }

            override fun onTick(millisUntilFinished: Long) {
                pbProgress.progress++
            }
        }.start()
    }
}
