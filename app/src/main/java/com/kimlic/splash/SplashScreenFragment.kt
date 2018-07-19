package com.kimlic.splash

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kimlic.BaseDialogFragment
import com.kimlic.R
import com.kimlic.utils.AppDuration
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import java.lang.ref.WeakReference

class SplashScreenFragment : BaseDialogFragment() {

    // Variables

    private lateinit var splashLogoWeakReference: WeakReference<ImageView>
    private var yoyoFadeIn: YoYo.YoYoString? = null
    private var yoyoFadeOut: YoYo.YoYoString? = null

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance() = SplashScreenFragment()
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_splash_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun playAnimation() { }

    override fun onDismiss(dialog: DialogInterface?) {
        yoyoFadeIn?.stop()
        yoyoFadeIn = null
        yoyoFadeOut?.stop()
        yoyoFadeOut = null
        super.onDismiss(dialog)
    }

    // Private

    private fun setupUI() {
        splashLogoWeakReference = WeakReference(logoIv)
        playFirstAnimation()
    }

    private fun secondAnimation() {
        yoyoFadeIn = YoYo
                .with(Techniques.FadeIn)
                .duration(AppDuration.SPLASH.duration).onEnd { playAnimation() } //getCallback().callback() }
                .playOn(splashLogoWeakReference.get())
    }

    fun playFirstAnimation() {
        yoyoFadeOut = YoYo
                .with(Techniques.FadeOut)
                .duration(AppDuration.SPLASH.duration)
                .onEnd { secondAnimation() }
                .playOn(splashLogoWeakReference.get())
    }
}
