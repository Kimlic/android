package com.kimlic.camera_id_verification

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_verify_document.*

class DocumentVerifyChooseActivity : BaseActivity() {

    // Variables

    private lateinit var documentType: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_document)

        setupUI()
    }

    private fun setupUI() {
        passportBt.setOnClickListener { documentType = "passport"; PresentationManager.passportVerify(this@DocumentVerifyChooseActivity) }
        driversBt.setOnClickListener { documentType = "driver"; }
        idCardBt.setOnClickListener { }

        backBt.setOnClickListener { finish() }
    }

}