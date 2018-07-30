package com.kimlic.documents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.documents.fragments.DocumentBillFragment
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.PhotoCallback
import com.kimlic.utils.UserPhotos

class BillActivity : BaseActivity() {

    // Variables

    private lateinit var billFragment: DocumentBillFragment
    private lateinit var fileName: String

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
        fileName = Prefs.currentAccountAddress + "_" + UserPhotos.bill.fileName
        val documentBillId = model.addUserDocument(Prefs.currentAccountAddress, Document(type = "bill"))

        initFragments()

        billFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String) {
                //val billDocument = Document(type = "bill", )
                Log.d("TAGBILL", fileName)
                val intent = Intent()
                intent.putExtra(AppConstants.filePathRezult.key, fileName)

                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        showFragment(R.id.container, billFragment, DocumentBillFragment.FRAGMENT_KEY)
    }

    private fun initFragments() {
        val bundle = Bundle()
        bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraRear.intKey)
        bundle.putString(AppConstants.filePathRezult.key, fileName)
        billFragment = DocumentBillFragment.newInstance(bundle)
    }
}
