package com.kimlic.scanner

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.journeyapps.barcodescanner.CaptureManager
import com.kimlic.BaseActivity
import com.kimlic.R
import kotlinx.android.synthetic.main.activity_scanner.*

class ScannerActivity : BaseActivity() {

    // Variables

    private var capture: CaptureManager? = null

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    // Private

    private fun setupUI() {
        capture = CaptureManager(this, zxing_barcode_scanner)
        capture!!.decode()

        cancelBt.setOnClickListener { finish() }
        backBt.setOnClickListener { finish() }
        setupTitle()
    }

    private fun setupTitle() {
        val spanText = getString(R.string.scan_the_qr_code_to_login)
        val words = spanText.split(" ")
        val spanEnd = words[0].length
        val spannableBuilder = SpannableStringBuilder(spanText)
        val boldStyle = StyleSpan(Typeface.BOLD)

        spannableBuilder.setSpan(boldStyle, 0, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        documenTitleTv.text = spannableBuilder
    }
}
