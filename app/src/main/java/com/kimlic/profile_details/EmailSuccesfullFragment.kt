package com.kimlic.profile_details

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseDialogFragment
import com.kimlic.BaseFragment
import com.kimlic.R

class EmailSuccesfullFragment : BaseDialogFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java

        fun newInstance(bundle: Bundle = Bundle()): EmailSuccesfullFragment {
            val fragment = EmailSuccesfullFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_email_successfull, container, false)
    }

}