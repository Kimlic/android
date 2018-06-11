package com.kimlic.terms

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
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_terms.*

class TermsActivity : BaseActivity() {

    // Constants

    private val TERMS_FILE_PATH_URL = "file:///android_asset/terms.html"
    private val PRIVACY_FILE_PATH_URL = "file:///android_asset/privacy.html"

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
        val action = intent.extras.getString("action", "accept")

        when (action) {
            "accept" -> {
                acceptBt.text = getString(R.string.accept_)
                acceptBt.setOnClickListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                cancelTv.visibility = View.VISIBLE
                cancelTv.setOnClickListener { setResult(Activity.RESULT_CANCELED); finish() }
            }
            "review" -> {
                acceptBt.text = getString(R.string.close)
                acceptBt.setOnClickListener { finish() }
            }
            else -> throw RuntimeException("Invalid document type")
        }
    }

    private fun fillWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = ProgressBar.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        webView.setBackgroundColor(Color.TRANSPARENT)

        val content = intent.extras.getString("content", "terms")

        when (content) {
            "terms" -> {
                titleTv.text = getString(R.string.terms_amp_conditions)
                subtitleTv.text = getString(R.string.last_modified_december_5_2017)
                webView.loadUrl(TERMS_FILE_PATH_URL)
            }
            "privacy" -> {
                titleTv.text = getString(R.string.privacy_policy)
                subtitleTv.text = getString(R.string.last_modified_december_5_2017)
                webView.loadUrl(PRIVACY_FILE_PATH_URL)
            }
            else -> throw RuntimeException("Invalid document content")
        }
    }
}
