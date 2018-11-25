package com.kimlic.passcode

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.utils.MessageCallback
import kotlinx.android.synthetic.main.fragment_locked_info.*
import java.util.concurrent.TimeUnit

class LockedInfoFragment : BasePopupFragment() {

    // Variables

    private lateinit var callback: MessageCallback
    private var timer: CountDownTimer? = null

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): LockedInfoFragment {
            return LockedInfoFragment().apply {
                this.arguments = bundle
            }
        }
    }

    // Live

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_locked_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_background_fragment)
    }

    // Public

    fun setCallback(callback: MessageCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        isCancelable = false
        val timeToUnlock = arguments!!.getLong(AppConstants.UNLOCK_TIME.key, 30000)

        val delay = timeToUnlock - System.currentTimeMillis()

        timer = object : CountDownTimer(delay, 1000) {
            private var remainedDelay = delay

            override fun onFinish() {
                timer = null; callback.callback(AppConstants.TIME.key)
            }

            override fun onTick(millisUntilFinished: Long) {
                remainedDelay -= 1000
                val hms = String.format("%02d : %02d : %02d"
                        , TimeUnit.MILLISECONDS.toHours(remainedDelay)
                        , TimeUnit.MILLISECONDS.toMinutes(remainedDelay) % TimeUnit.HOURS.toMinutes(1)
                        , TimeUnit.MILLISECONDS.toSeconds(remainedDelay) % TimeUnit.MINUTES.toSeconds(1)
                )
                subtitleTv?.text = hms
            }
        }.start()

        laterBt.setOnClickListener { callback.callback(AppConstants.LATER.key) }
        dismissBt.setOnClickListener { callback.callback(AppConstants.LATER.key) }
    }
}