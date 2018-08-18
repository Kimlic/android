package com.kimlic

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.splash.SplashScreenFragment
import java.util.*

class MainActivity : BaseActivity() {

    // Variables

    private lateinit var splashFragment: SplashScreenFragment
    private lateinit var model: ProfileViewModel
    private val timeQueue = ArrayDeque<Long>(listOf(2000L, 4000L))

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        initFragment()
        splashScreenShow()

        if (Prefs.authenticated) {
            syncProfile(); quorumRequest()
        } else signUpRecovery()
    }

    private fun initFragment() {
        splashFragment = SplashScreenFragment.newInstance()
    }

    private fun splashScreenHide() = splashFragment.dismiss()

    private fun splashScreenShow() = splashFragment.show(supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)

    private fun syncProfile() = model.syncProfile()

    private fun quorumRequest() {
        model.quorumRequest(
                onSuccess = { continueApp() },
                onError = { retry() }
        )
    }

    private fun continueApp() {
        object : CountDownTimer(1500, 1500) {
            override fun onFinish() {
                if (Prefs.isPasscodeEnabled) {
                    if (Prefs.isTouchEnabled) {
                        PresentationManager.login(this@MainActivity)
                    } else PresentationManager.passcodeUnlock(this@MainActivity)
                } else PresentationManager.stage(this@MainActivity)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun signUpRecovery() {
        object : CountDownTimer(3000, 3000) {
            override fun onFinish() {
                splashScreenHide(); PresentationManager.signupRecovery(this@MainActivity)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun retry() = timeQueue.poll()?.let { Handler().postDelayed({ quorumRequest() }, it) } ?: errorPopup("server error")
}