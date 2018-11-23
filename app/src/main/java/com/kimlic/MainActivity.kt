package com.kimlic

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.kimlic.db.KimlicDB
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.service.CompanyDetailsSyncService
import com.kimlic.splash.SplashScreenFragment

class MainActivity : BaseActivity() {

    // Variables

    private lateinit var splashFragment: SplashScreenFragment
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        initDatabaseFile()
        initFragment()
//        val intent = Intent(this, DocumentVerifyActivity_test::class.java)
//        startActivity(intent)
        splashScreenShow()

        if (Prefs.authenticated) {
            companySyncService()
            syncProfile()
            quorumRequest()
        } else signUpRecovery()
    }

    private fun initFragment() {
        splashFragment = SplashScreenFragment.newInstance()
    }

    private fun splashScreenHide() {
        try {
            splashFragment.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun splashScreenShow() = splashFragment.show(this.supportFragmentManager, SplashScreenFragment.FRAGMENT_KEY)

    private fun syncProfile() = model.syncProfile()

    private fun companySyncService() {
        val syncIntent = Intent(this, CompanyDetailsSyncService::class.java)
        startService(syncIntent)
    }

    private fun initDatabaseFile() {
        KimlicDB.getInstance()!!.userDao().select("kimlic")
    }

    private fun quorumRequest() {
        model.quorumRequest(
                onSuccess = { continueApp() },
                onError = { errorPopup("server error") }
        )
    }

    private fun continueApp() {
        object : CountDownTimer(1500, 1500) {
            override fun onFinish() {
                if (Prefs.isPasscodeEnabled) {
                    (application as KimlicApp).isFirstTime = false
                    PresentationManager.passcodeUnlock(this@MainActivity)
                } else {
                    (application as KimlicApp).isFirstTime = false
                    PresentationManager.stage(this@MainActivity)
                }
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun signUpRecovery() {
        object : CountDownTimer(3000, 3000) {
            override fun onFinish() {
                splashScreenHide(); PresentationManager.signUpRecovery(this@MainActivity)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }
}