package com.kimlic.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_error.*

class ErrorPopupFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstanse(bundle: Bundle = Bundle()): ErrorPopupFragment {
            val fragment = ErrorPopupFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    // Private

    private fun setupUI() {
        val error = arguments!!.getString(AppConstants.errorDescription.key, getString(R.string.error))
        errorTv.text = error
        isCancelable = true
    }
}