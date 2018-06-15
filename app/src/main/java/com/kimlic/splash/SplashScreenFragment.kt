package com.kimlic.splash

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kimlic.BaseDialogFragment
import com.kimlic.R
import com.kimlic.utils.AppDuration
import kotlinx.android.synthetic.main.fragment_splash_screen.*

class SplashScreenFragment : BaseDialogFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance() = SplashScreenFragment()
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun playAnimation() {
        YoYo
                .with(Techniques.FadeOut)
                .duration(AppDuration.SPLASH.duration)
                .onEnd { secondAnimation() }
                .playOn(logoIv)
    }

    private fun secondAnimation() {
        YoYo
                .with(Techniques.FadeIn)
                .duration(AppDuration.SPLASH.duration).onEnd { getCallback().callback() }
                .playOn(logoIv)
    }

}
