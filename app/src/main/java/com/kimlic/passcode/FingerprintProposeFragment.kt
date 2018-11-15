package com.kimlic.passcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.managers.FingerprintService
import com.kimlic.utils.MessageCallback
import kotlinx.android.synthetic.main.fragment_fingerprint_propose.*

class FingerprintProposeFragment : BasePopupFragment() {

    // Variables

    private var fingerprintService: FingerprintService? = null
    private lateinit var callback: MessageCallback

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): FingerprintProposeFragment {
            val fragment = FingerprintProposeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Live

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fingerprint_propose, container, false)
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

        fingerprintService = FingerprintService(activity!!, onSuccess = {
            callback.callback("success")
        }, onFail = {
            this?.dismiss()// TODO bug in fingerPrint!!!
            callback.callback("error")
        })

        insteadBt.setOnClickListener { fingerprintService = null; dismiss() }
        dismissBt.setOnClickListener { fingerprintService = null; dismiss() }
    }
}