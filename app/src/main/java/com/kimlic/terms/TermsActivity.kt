package com.kimlic.terms

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.activity_terms.*

class TermsActivity : BaseActivity() {

    // Constants

    companion object {
        private const val TERMS_FILE_PATH_URL = "file:///android_asset/terms.html"
        private const val PRIVACY_FILE_PATH_URL = "file:///android_asset/privacy.html"
    }

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        setupUI()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    // Private

    private fun setupUI() {
        fillWebView()
        assignButtonState()
    }

    private fun assignButtonState() {
        val action = intent?.extras?.getString(AppConstants.ACTION.key, AppConstants.ACCEPT.key).orEmpty()

        when (action) {
            AppConstants.ACCEPT.key -> {
                confirmBt.text = getString(R.string.accept_)
                confirmBt.setOnClickListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                changeTv.visibility = View.VISIBLE
                changeTv.setOnClickListener { setResult(Activity.RESULT_CANCELED); finish() }
            }
            AppConstants.REVIEW.key -> {
                confirmBt.text = getString(R.string.close)
                confirmBt.setOnClickListener { finish() }
            }
            else -> throw RuntimeException("Invalid document type")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun fillWebView() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = ProgressBar.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view!!.loadUrl("javascript:document.body.style.setProperty(\"color\", \"white\");")
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        webView.setBackgroundColor(Color.TRANSPARENT)

        val content = intent?.extras?.getString(AppConstants.CONTENT.key, AppConstants.TERMS.key).orEmpty()

        when (content) {
            AppConstants.TERMS.key -> {
                titleTv.text = getString(R.string.terms_amp_conditions)
                subtitleTv.text = getString(R.string.last_modified_24_october_2018)
                webView.loadUrl(TERMS_FILE_PATH_URL)
            }
            AppConstants.PRIVACY.key -> {
                titleTv.text = getString(R.string.privacy_policy)
                subtitleTv.text = getString(R.string.last_modified_24_october_2018)
                webView.loadUrl(PRIVACY_FILE_PATH_URL)
            }
            else -> throw RuntimeException("Invalid document content")
        }
    }
}