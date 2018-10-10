package com.kimlic.documents.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.camera.CameraBaseFragment
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_document_portrait.*

class PortraitPhotoFragment : CameraBaseFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance(bundle: Bundle = Bundle()): PortraitPhotoFragment {
            val fragment = PortraitPhotoFragment()
            bundle.putInt(AppConstants.CAMERA_TYPE.key, AppConstants.CAMERA_FRONT.intKey)
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_document_portrait, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    // Private

    private fun setupUI() {
        val action = arguments?.getString("action", "")

        when (action) {
            AppConstants.PORTRAIT_DOCUMENT.key -> {
                setDocumentBackground()
            }
        }

        backBt.setOnClickListener { activity!!.finish() }
        setupTitle()
    }

    private fun setupTitle() {
        val spanText = getString(R.string.take_your_portrait)
        val words = spanText.split(" ")
        val spanStart = words[0].length + words[1].length + 2
        val spannableBuilder = SpannableStringBuilder(spanText)
        val boldStyle = StyleSpan(android.graphics.Typeface.BOLD)

        spannableBuilder.setSpan(boldStyle, spanStart, spanText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        documenTitleTv.text = spannableBuilder
    }

    private fun setDocumentBackground() {
        auxilaryContourIv.setBackgroundResource(R.drawable.ic_id_validation_white_contour_and_mask_bakcground)
        documentTypeIv.visibility = View.GONE
        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        auxilaryContourIv.layoutParams = layoutParams
    }
}