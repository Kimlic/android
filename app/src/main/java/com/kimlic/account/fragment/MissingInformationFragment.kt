package com.kimlic.account.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_missing_information.*

class MissingInformationFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun getInstance(bundle: Bundle = Bundle()): MissingInformationFragment {
            val fragment = MissingInformationFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Live

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_missing_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    // Private

    private fun setupUI() {
        isCancelable = true
        dismissBt.setOnClickListener { dismiss() }
        updateBt.setOnClickListener { dismiss() }
    }
}