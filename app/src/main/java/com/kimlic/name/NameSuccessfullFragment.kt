package com.kimlic.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseDialogFragment
import com.kimlic.R

class NameSuccessfullFragment : BaseDialogFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java

        fun newInstance(bundle: Bundle = Bundle()): NameSuccessfullFragment {
            val fragment = NameSuccessfullFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_name_successfull, container, false)
    }

}