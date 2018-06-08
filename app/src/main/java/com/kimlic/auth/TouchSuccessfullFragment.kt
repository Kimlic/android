package com.kimlic.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseDialogFragment
import com.kimlic.R

class TouchSuccessfullFragment : BaseDialogFragment() {

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(): TouchSuccessfullFragment {
            return TouchSuccessfullFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_touch_successfull, container, false)
    }
}