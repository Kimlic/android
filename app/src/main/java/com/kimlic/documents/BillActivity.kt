package com.kimlic.documents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.DocumentBillFragment
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback

class BillActivity : BaseActivity() {

    // Variables

    private lateinit var billFragment: DocumentBillFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        setupUI()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        initFragments()

        billFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                val intent = Intent()
                intent.putExtra(AppConstants.DOCUMENT_BYTE_ARRAY.key, data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        showFragment(R.id.container, billFragment, DocumentBillFragment.FRAGMENT_KEY)
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.CAMERA_TYPE.key, AppConstants.CAMERA_REAR.intKey)
        billFragment = DocumentBillFragment.newInstance(bundle)
    }
}
