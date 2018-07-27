package com.kimlic.verification

import android.os.Bundle
import android.util.Log
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.utils.AppConstants
import com.kimlic.verification.fragments.PortraitPhotoFragment
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.PhotoCallback
import com.kimlic.utils.UserPhotos
import com.kimlic.verification.fragments.DocumentBillFragment

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
            override fun callback(fileName: String) {
                Log.d("TAGBILL", fileName)
            }
        })

//        billFragment.setCallback(object : BaseCallback {
//            override fun callback() {
//                finish()
//            }
//        })
        showFragment(R.id.container, billFragment, DocumentBillFragment.FRAGMENT_KEY)
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraRear.intKey)
        bundle.putString(AppConstants.filePathRezult.key, UserPhotos.bill.fileName)
        billFragment = DocumentBillFragment.newInstance(bundle)
    }
}
