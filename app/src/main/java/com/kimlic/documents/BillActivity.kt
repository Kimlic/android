package com.kimlic.documents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            override fun callback(fileName: String, data: ByteArray) {// TODO byte array
                Log.d("TAGBILL", fileName)
                val intent = Intent()
                intent.putExtra(AppConstants.documentByteArray.key, data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        showFragment(R.id.container, billFragment, DocumentBillFragment.FRAGMENT_KEY)
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraRear.intKey)
        billFragment = DocumentBillFragment.newInstance(bundle)
    }
}
