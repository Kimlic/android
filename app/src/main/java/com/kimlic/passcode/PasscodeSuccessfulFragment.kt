package com.kimlic.passcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseDialogFragment

import com.kimlic.R

class PasscodeSuccessfulFragment : BaseDialogFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance(): PasscodeSuccessfulFragment {
            return PasscodeSuccessfulFragment()
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_passcode_successfull, container, false)
    }

}