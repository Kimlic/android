package com.kimlic.stage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_phone_verify.*

class PhoneVerifyFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance() = PhoneVerifyFragment()
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    // Private

    private fun setupUI() {
        titleTv.text = getString(R.string.verify_your_number_)
        subtitleTv.text = getString(R.string.number_is_not_verified)

        upperView.visibility = View.VISIBLE
        upperBt.text = getString(R.string.change_number_)
        upperBt.setOnClickListener { activity!!.showToast("Change number is pressed") }

        lowerBt.text = getString(R.string.send_verification_code)
        lowerBt.setOnClickListener { activity!!.showToast("Send verification code is pressed") }

        dismissBt.setOnClickListener{activity!!.showToast("Dismiss closed")}
    }
}