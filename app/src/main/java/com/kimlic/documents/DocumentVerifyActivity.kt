package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.documents.fragments.DocumentBackFragment
import com.kimlic.documents.fragments.DocumentFrontFragment
import com.kimlic.documents.fragments.PortraitPhotoFragment
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.Cache
import com.kimlic.utils.PhotoCallback
import org.spongycastle.util.encoders.Base64
import java.io.IOException
import java.io.OutputStreamWriter

class DocumentVerifyActivity : BaseActivity() {

    // Constants

    companion object {
        private const val DOCUMENT_TAKE_PHOTO_REQUEST_CODE = 1445
    }

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var frontFragment: DocumentFrontFragment
    private lateinit var backFragment: DocumentBackFragment

    private lateinit var documentType: String
    private lateinit var country: String

    private lateinit var portraitData: ByteArray
    private lateinit var frontData: ByteArray
    private lateinit var backData: ByteArray
    private lateinit var model: ProfileViewModel

    // Life

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DOCUMENT_TAKE_PHOTO_REQUEST_CODE -> {
                setResult(RESULT_OK); finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount < 2) finish()
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        documentType = intent.extras.getString(AppConstants.DOCUMENT_TYPE.key, "")
        country = intent.extras.getString(AppConstants.COUNTRY.key, "")

        initFragments()

        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)

        portraitFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                portraitData = data
                showFragment(R.id.container, frontFragment, DocumentFrontFragment.FRAGMENT_KEY)
            }
        })
        frontFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                frontData = data
                showFragment(R.id.container, backFragment, DocumentBackFragment.FRAGMENT_KEY)
            }
        })
        backFragment.setCallback(object : PhotoCallback {
            override fun callback(data: ByteArray) {
                backData = data

                saveTempFileToDisk(Cache.PORTRAIT.file, portraitData)
                saveTempFileToDisk(Cache.FRONT.file, frontData)
                saveTempFileToDisk(Cache.BACK.file, backData)

                PresentationManager.detailsDocumentSave(this@DocumentVerifyActivity, documentType, country, DOCUMENT_TAKE_PHOTO_REQUEST_CODE)
            }
        })
    }

    private fun initFragments() {
        val portraitBundle = Bundle()
        portraitBundle.putInt(AppConstants.CAMERA_TYPE.key, AppConstants.CAMERA_FRONT.intKey)
        portraitBundle.putString("action", AppConstants.PORTRAIT_DOCUMENT.key)
        portraitFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        frontFragment = DocumentFrontFragment.newInstance()
        backFragment = DocumentBackFragment.newInstance()
    }

    private fun saveTempFileToDisk(fileName: String, data: ByteArray) {
        val data64 = Base64.toBase64String(data)

        try {
            val outputStreamWriter = OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(data64)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("TAG", "FileWriteFailed- " + e.toString())
        }
    }
}