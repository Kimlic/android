package com.kimlic.documents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.Photo
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.PhotoCallback
import com.kimlic.utils.UserPhotos
import com.kimlic.documents.fragments.DocumentBackFragment
import com.kimlic.documents.fragments.DocumentFrontFragment
import com.kimlic.documents.fragments.PortraitPhotoFragment
import java.io.File

class DocumentVerifyActivity : BaseActivity() {

    // Variables

    private lateinit var portraitFragment: PortraitPhotoFragment
    private lateinit var frontFragment: DocumentFrontFragment
    private lateinit var backFragment: DocumentBackFragment

    private lateinit var portraitFilePath: String
    private lateinit var documentFrontSideFilePath: String
    private lateinit var documentBackSideFilePath: String
    private lateinit var documentType: String
    private val defaultPath = UserPhotos.default.fileName

    private lateinit var portraitPhoto: Photo
    private lateinit var documentFrontPhoto: Photo
    private lateinit var documentBackPhoto: Photo

    private var documentId: Long = 0
    private var filesList = mutableListOf<String>()
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_verify)

        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.getBackStackEntryCount() < 2) {
            finish(); deleteFotosAndDocument()
        }
        super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        documentType = intent.extras.getString(AppConstants.documentType.key, "")
        initFragments()

        documentId = model.addUserDocument(Prefs.currentAccountAddress, Document(type = documentType))

        showFragment(R.id.container, portraitFragment, PortraitPhotoFragment.FRAGMENT_KEY)

        portraitFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String) {
                filesList.add(fileName)
                portraitPhoto = Photo(documentId = documentId, file = fileName, side = "portrait")
                showFragment(R.id.container, frontFragment, DocumentFrontFragment.FRAGMENT_KEY)
            }
        })

        frontFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String) {
                filesList.add(fileName)
                documentFrontPhoto = Photo(documentId = documentId, file = fileName, side = "front")
                showFragment(R.id.container, backFragment, DocumentBackFragment.FRAGMENT_KEY)
            }
        })

        backFragment.setCallback(object : PhotoCallback {
            override fun callback(fileName: String) {
                filesList.add(fileName)
                documentBackPhoto = Photo(documentId = documentId, file = fileName, side = "back")
                model.addPhotosForDocument(portraitPhoto, documentFrontPhoto, documentBackPhoto)
                successfull()
            }
        })
    }

    private fun initFragments() {
        val accountAddress = KimlicDB.getInstance()!!.userDao().select(Prefs.currentId).accountAddress

        portraitFilePath = accountAddress + "_" + intent.extras.getString(UserPhotos.portraitFilePath.fileName, defaultPath)
        documentFrontSideFilePath = accountAddress + "_" + intent.extras.getString(UserPhotos.frontFilePath.fileName, defaultPath)
        documentBackSideFilePath = accountAddress + "_" + intent.extras.getString(UserPhotos.backFilePath.fileName, defaultPath)

        val portraitBundle = Bundle()
        val frontBundle = Bundle()
        val backBundle = Bundle()

        portraitBundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)

        portraitBundle.putString(AppConstants.filePathRezult.key, portraitFilePath)
        frontBundle.putString(AppConstants.filePathRezult.key, documentFrontSideFilePath)
        backBundle.putString(AppConstants.filePathRezult.key, documentBackSideFilePath)

        portraitFragment = PortraitPhotoFragment.newInstance(portraitBundle)
        frontFragment = DocumentFrontFragment.newInstance(frontBundle)
        backFragment = DocumentBackFragment.newInstance(backBundle)
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

    private fun deleteFotosAndDocument() {
        filesList.forEach {
            val path = filesDir.toString() + "/" + it
            if (File(path).exists()) {
                File(path).delete()
            }
        }
        model.dropDocument(documentId)
    }
}
