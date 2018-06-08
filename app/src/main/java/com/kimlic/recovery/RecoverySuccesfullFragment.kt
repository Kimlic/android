package com.kimlic.recovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseDialogFragment
import com.kimlic.R

class RecoverySuccesfullFragment : BaseDialogFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(): RecoverySuccesfullFragment {
            return RecoverySuccesfullFragment()
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recovery_successfull, container, false)
    }
}