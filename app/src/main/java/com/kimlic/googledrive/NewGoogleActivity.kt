package com.kimlic.googledrive

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.kimlic.BaseActivity
import com.kimlic.R
import kotlinx.android.synthetic.main.activity_new_google.*
import java.io.OutputStreamWriter
import java.util.*

class NewGoogleActivity : BaseActivity() {

    // Constants

    private val REQUEST_CODE_SIGN_IN = 1

    // Variables

    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account

    private var mDriveClient: DriveClient? = null //Handles high-level drive functions like sync

    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_google)
        signIn()

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                if (resultCode != Activity.RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.d("TAG", "Sign-in failed.")
                    finish()
                    return
                }

                val getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                if (getAccountTask.isSuccessful) {
                    initializeDriveClient(getAccountTask.result)
                } else {
                    Log.e("TAG", "Sign-in failed.")
                    finish()
                }
            }

//            REQUEST_CODE_OPEN_ITEM -> if (resultCode == Activity.RESULT_OK) {
//                val driveId = data.getParcelableExtra<DriveId>(
//                        OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID)
//                mOpenItemTaskSource.setResult(driveId)
//            } else {
//                mOpenItemTaskSource.setException(RuntimeException("Unable to open file"))
//            }
        }
    }

    // onActivityResult

    protected fun signIn() {
        val requiredScopes = HashSet<Scope>(2)
        requiredScopes.add(Drive.SCOPE_FILE)
        requiredScopes.add(Drive.SCOPE_APPFOLDER)
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (mGoogleSignInAccount != null && mGoogleSignInAccount?.grantedScopes!!.containsAll(requiredScopes)) {
            initializeDriveClient(mGoogleSignInAccount!!)
        } else {
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Drive.SCOPE_FILE)
                    .requestScopes(Drive.SCOPE_APPFOLDER)
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(this, signInOptions)
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun initializeDriveClient(signInAccount: GoogleSignInAccount) {
        mDriveClient = Drive.getDriveClient(applicationContext, signInAccount)
        mDriveResourceClient = Drive.getDriveResourceClient(applicationContext, signInAccount)
        // onDriveClientReady()
    }


    private fun setupUI() {
        signoutBt.setOnClickListener({ signOut() })
        createFolderBt.setOnClickListener({ createFolder() })
        queryBt.setOnClickListener { querryFile() }

    }

    private fun signOut() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInCliet = GoogleSignIn.getClient(this, googleSignInOptions)
        googleSignInCliet.signOut()
    }

    private fun createFolder() {
        val appFolder = mDriveResourceClient!!.appFolder
        //val simpleFolder = mDriveResourceClient!!.rootFolder.result

        mDriveResourceClient!!
                .rootFolder
                .continueWithTask {
                    val parentFolder = it.getResult()
                    // appFolder_ = appFolder.result
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle("Kimlic!!!").setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build()

                    mDriveResourceClient!!.createFolder(parentFolder, metadataChangeSet)
                }
                .addOnSuccessListener {

                    createFileInFolder(it)
                    Log.d("TAGFOLDER", "Succsess!!! = " + it.toString())
                }
                .addOnFailureListener {
                    Log.d("TAGFOLDER", "exception e = " + it.toString())
                }
    }

    private fun createFileInFolder(parent: DriveFolder) {
        mDriveResourceClient!!
                .createContents().continueWithTask {
                    val contents = it.getResult()
                    val outputStream = contents.outputStream

                    OutputStreamWriter(outputStream).use { writer -> writer.write("Hello World!") }

                    val metadataChangeSet = MetadataChangeSet.Builder()
                            .setTitle("KimlicFile.txt")
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build()

                    mDriveResourceClient!!.createFile(parent, metadataChangeSet, contents)
                }
                .addOnSuccessListener(this, OnSuccessListener {
                    Log.d("TAGFILE", "file created!!!")
                })
                .addOnFailureListener(this, OnFailureListener {     Log.d("TAGFILE", "exception e = " + it.toString())
                })


    }

    fun querryFile() {

        val querry = Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, "KimlickFile")).build()


        val querryTask = mDriveResourceClient!!
                .query(querry)
                .addOnSuccessListener { metadataBuffer -> Log.d("TAGFILEQUERRY", "someinfo") }
                .addOnFailureListener {
                    Log.d("TAGFILEQUERRY", "querry failed")
                }
    }
}

