package com.kimlic

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kimlic.utils.BaseCallback


abstract class BaseDialogFragment : DialogFragment() {

    // Constants

    private val DEFAULT_ANIMATION_DURATION = 2000L
    private val DEFSULT_ANIMATION_REPEAT = 1

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_passcode_successfull, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    // Public

    fun setCallback(callback: BaseCallback) {
        this.callback = callback
    }

    fun getCallback() = this.callback

    // Private

    private fun setupUI() {
        isCancelable = false
        playAnimation()

    }

    open fun playAnimation() {
        val targetAnimation = view?.findViewById<ImageView>(R.id.logoIv)

        YoYo
                .with(Techniques.FadeIn)
                .repeat(DEFSULT_ANIMATION_REPEAT)
                .duration(DEFAULT_ANIMATION_DURATION)
                .interpolate(AccelerateDecelerateInterpolator())
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .withListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        callback.callback()
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}
                })
                .playOn(targetAnimation)
    }

}