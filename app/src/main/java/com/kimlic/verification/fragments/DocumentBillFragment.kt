package com.kimlic.verification.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.camera.CameraBaseFragment
import kotlinx.android.synthetic.main.fragment_document_card.*

class DocumentBillFragment : CameraBaseFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle? = Bundle()): DocumentBillFragment {
            val fragment = DocumentBillFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_document_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    // Private

    private fun setupUI() {
        backBt.setOnClickListener { activity!!.finish() }
        setupTitle()
    }

    private fun setupTitle() {
        val spanText = getString(R.string.take_a_photo_of_bill)
        val words = spanText.split(" ")
        val spanStart = words[0].length + words[1].length + words[2].length + words[3].length + 4
        val spannableBuilder = SpannableStringBuilder(spanText)
        val boldStyle = StyleSpan(Typeface.BOLD)

        spannableBuilder.setSpan(boldStyle, spanStart, spanText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        documenTitleTv.text = spannableBuilder
    }

}