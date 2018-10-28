package com.kimlic.auth

import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.FingerprintService
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppDuration
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_touch_id.*

class TouchIdActivity : BaseActivity() {

    // Variables

    var fingerprintService: FingerprintService? = null
    private lateinit var action: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_id)

        setupUI()
    }

    override fun onBackPressed() {
        fingerprintService = null
        finish()
        //super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        animation()
        action = intent.extras.getString("action", "unlock")
        Log.d("TAGWAS", "touchidActivity on create action = ${action}")

        cancelTv.setOnClickListener { finish() }
        when (action) {
            "create" -> {
                fingerprintService = FingerprintService(this, { create() }, { showToast(it); finish() })
            }
            "disable" -> {
                fingerprintService = FingerprintService(this, { disable() }, { showToast(it); finish() })
            }
            "unlock" -> {
                propouseTouch()
                fingerprintService = FingerprintService(this, { unlock() }, { fingerprintService = null; showToast(it); passcodeUnlock() })
            }
//            "unlock_finish" -> {
//                propouseTouch()
//                fingerprintService = FingerprintService(this
//                        , onSuccess = {
//                    (application as KimlicApp).wasInBackground = false
//                    finish()
//                    //PresentationManager.stage(this)
//                }, onFail = {
//                    Log.d("TAGWAS", "in touchId on FAIL!!!")
//                    if (fingerprintService != null) {
//                        fingerprintService = null; showToast(it); finish()
//                    } else {
//                        finish()
//                        PresentationManager.passcodeFinish(this)
//                    }
//                })
//
//                cancelTv.setOnClickListener { fingerprintService = null; finish();passcodeFinish() }
//            }
            else -> throw RuntimeException("Invalid action")
        }
    }

    private fun create() {
        Prefs.isTouchEnabled = true
        successful()
    }

    private fun disable() {
        Prefs.isTouchEnabled = false
        finish()
    }

    private fun unlock() {
        PresentationManager.stage(this)
    }

    private fun propouseTouch() {
        val touchFragment = TouchIDFragment.newInstance()

        touchFragment.setCallback(object : BaseCallback {
            override fun callback() {
                if (action == "unlock_finish") {
                    fingerprintService = null
                    finish()
                    passcodeFinish()
                } else {
                    fingerprintService = null
                    passcodeUnlock()
                }
            }
        })
        touchFragment.show(supportFragmentManager, TouchIDFragment.FRAGMENT_KEY)
    }

    private fun passcodeUnlock() {
        fingerprintService = null
        PresentationManager.passcodeUnlock(this)
    }

    private fun passcodeFinish() {
        fingerprintService = null
        finish()
//        PresentationManager.passcodeFinish(this)
    }

    private fun animation() {
        YoYo
                .with(Techniques.Tada)
                .repeat(YoYo.INFINITE)
                .duration(AppDuration.FINGERPRINT_DURATION.duration)
                .interpolate(AccelerateDecelerateInterpolator())
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .playOn(image)
    }

    private fun successful() {
        val fragment = TouchSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@TouchIdActivity)
            }
        })
        fragment.show(supportFragmentManager, TouchSuccessfulFragment.FRAGMENT_KEY)
    }
}