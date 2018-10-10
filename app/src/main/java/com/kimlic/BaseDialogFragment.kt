package com.kimlic

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kimlic.utils.AppDuration
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.fragment_phone_successfull.*

abstract class BaseDialogFragment : DialogFragment() {

    // Variables

    var activity: BaseActivity? = null
    private lateinit var callback: BaseCallback

    // Life

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is BaseActivity)
            activity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LOW_PROFILE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_passcode_successfull, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    // Public

    fun setCallback(callback: BaseCallback) {
        this.callback = callback
    }

    open fun playAnimation() {
        YoYo
                .with(Techniques.FadeOut)
                .duration(AppDuration.SUCCESSFUL_DURATION.duration)
                .onEnd { secondAnimation() }
                .playOn(logoRoundedIv)

        logoAnimation()
    }

    // Private

    private fun setupUI() {
        isCancelable = false
        playAnimation()

    }

    private fun secondAnimation() {
        YoYo
                .with(Techniques.FadeIn)
                .duration(AppDuration.SUCCESSFUL_DURATION.duration)
                .onEnd { callback.callback() }
                .playOn(logoRoundedIv)
    }

    private fun logoAnimation() {
        YoYo
                .with(Techniques.Shake)
                .duration(AppDuration.LOGO_DURATION.duration)
                .repeat(YoYo.INFINITE)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(AccelerateDecelerateInterpolator())
                .playOn(logoIv)
    }
}