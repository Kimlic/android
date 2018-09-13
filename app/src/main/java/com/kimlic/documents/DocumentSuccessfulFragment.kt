package com.kimlic.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseDialogFragment
import com.kimlic.R

class DocumentSuccessfulFragment : BaseDialogFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance() = DocumentSuccessfulFragment()
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_document_successful, container, false)
    }
}