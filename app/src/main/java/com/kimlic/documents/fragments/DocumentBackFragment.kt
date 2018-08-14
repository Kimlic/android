package com.kimlic.documents.fragments

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

class DocumentBackFragment : CameraBaseFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle? = Bundle()): DocumentBackFragment {
            val fragment = DocumentBackFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_document_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    // Private

    private fun setupUI() {
        backBt.setOnClickListener { activity!!.finish() }
        documentTypeIv.setBackgroundResource(R.drawable.ic_camera_screen_card_backside_icon)
        setupTitle()
    }

    private fun setupTitle() {
        val spanText = getString(R.string.back_side_of_the_document)
        val words = spanText.split(" ")
        val spanEnd = words[0].length + words[1].length + 1
        val spannableBuilder = SpannableStringBuilder(spanText)
        val boldStyle = StyleSpan(Typeface.BOLD)

        spannableBuilder.setSpan(boldStyle, 0, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        documenTitleTv.text = spannableBuilder
    }
}