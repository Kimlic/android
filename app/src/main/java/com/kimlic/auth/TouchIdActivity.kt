package com.kimlic.auth

import android.os.Bundle
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

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_id)

        setupUI()
    }

    // Private

    private fun setupUI() {
        animation()
        val action = intent.extras.getString("action", "unlock")

        when (action) {
            "create" -> {
                val fingerprintService = FingerprintService(this, { create() }, { showToast(it); finish() })
            }
            "disable" -> {
                val fingerprintService = FingerprintService(this, { disable() }, { showToast(it); finish() })
            }
            else -> throw RuntimeException("Invalid action")
        }

        cancelBt.setOnClickListener { finish() }
    }

    private fun create() {
        Prefs.isTouchEnabled = true
        successfull()
    }

    private fun disable() {
        Prefs.isTouchEnabled = false
        finish()
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

    private fun successfull() {
        val fragment = TouchSuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@TouchIdActivity)
            }
        })
        fragment.show(supportFragmentManager, TouchSuccessfullFragment.FRAGMENT_KEY)
    }
}
