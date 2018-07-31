package com.kimlic.documents

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.Photo
import com.kimlic.documents.fragments.DocumentBackFragment
import com.kimlic.documents.fragments.DocumentFrontFragment
import com.kimlic.documents.fragments.PortraitPhotoFragment
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.PhotoCallback
import com.kimlic.utils.UserPhotos

class DocumentVerifyActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var frontFragment: DocumentFrontFragment
    private lateinit var backFragment: DocumentBackFragment

    private lateinit var portraitFilePath: String
    private lateinit var documentFrontFilePath: String
    private lateinit var documentBackSideFilePath: String
    private lateinit var documentType: String
    private val defaultPath = UserPhotos.default.fileName

    private lateinit var portraitPhoto: Photo
    private lateinit var documentFrontPhoto: Photo
    private lateinit var documentBackPhoto: Photo

    private lateinit var portraitBitmap: Bitmap
    private lateinit var documentFrontBitmap: Bitmap
    private lateinit var documentBackBitmap: Bitmap

    private var documentId: Long = 0

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.getBackStackEntryCount() < 2) {
            finish(); deleteDocument()
        }
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        initFragments()

        documentId = model.addUserDocument(Prefs.currentAccountAddress, Document(type = documentType))
        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)

        portraitFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String, data: ByteArray) {
                portraitPhoto = Photo(documentId = documentId, file = portraitFilePath, type = "portrait")
                portraitBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

                showFragment(R.id.container, frontFragment, DocumentFrontFragment.FRAGMENT_KEY)
            }
        })
        frontFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String, data: ByteArray) {
                documentFrontPhoto = Photo(documentId = documentId, file = fileName, type = "front")
                documentFrontBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                showFragment(R.id.container, backFragment, DocumentBackFragment.FRAGMENT_KEY)
            }
        })
        backFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String, data: ByteArray) { //TODO byteArray
                documentBackPhoto = Photo(documentId = documentId, file = fileName, type = "back")
                documentBackBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

                Handler().post(Runnable {
                    saveBitmap(portraitFilePath, portraitBitmap)
                    saveBitmap(documentFrontFilePath, documentFrontBitmap)
                    saveBitmap(documentBackSideFilePath, documentBackBitmap)
                    model.addDocumentPhoto(portraitPhoto, documentFrontPhoto, documentBackPhoto)
                })
                successfull()
            }
        })
    }

    private fun initFragments() {
        val accountAddress = KimlicDB.getInstance()!!.userDao().select(Prefs.currentId).accountAddress

        portraitFilePath = accountAddress + "_" + intent.extras.getString(UserPhotos.portraitFilePath.fileName, defaultPath)
        documentFrontFilePath = accountAddress + "_" + intent.extras.getString(UserPhotos.frontFilePath.fileName, defaultPath)
        documentBackSideFilePath = accountAddress + "_" + intent.extras.getString(UserPhotos.backFilePath.fileName, defaultPath)

        val portraitBundle = Bundle()

        portraitBundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)

        portraitFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        frontFragment = DocumentFrontFragment.newInstance()//frontBundle)
        backFragment = DocumentBackFragment.newInstance()//backBundle)
    }

    private fun successfull() {
        val fragment = VerifySuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@DocumentVerifyActivity)
            }
        })

        fragment.show(supportFragmentManager, VerifySuccessfullFragment.FRAGMENT_KEY)
    }

    private fun deleteDocument() {
        model.dropDocument(documentId = documentId)
    }
}
