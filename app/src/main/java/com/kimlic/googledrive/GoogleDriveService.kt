package com.kimlic.googledrive

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.*
import com.google.android.gms.tasks.Task
import com.kimlic.BaseActivity
import com.kimlic.R
import kotlinx.android.synthetic.main.activity_google_drive.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class GoogleDriveService : BaseActivity() {

    // Constants


    private val REQUEST_CODE_SIGN_IN = 0
    private val REQUEST_CODE_CREATOR = 2
    // Variables

    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var driveClient: DriveClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_drive)


        setupUI()
        //googleSignInClient = buildGoogleSignInClient()

    }
    
    override fun onDestroy() {
        super.onDestroy()
    }

    // Private

    private fun setupUI() {
        signIn()

        signoutBt.setOnClickListener({ googleSignInClient!!.signOut() })
        createBt.setOnClickListener({ createFileonGoogleDrive() })

    }

    private fun createFileonGoogleDrive() {


    }

    private fun createFileIntentSender(driveContents: DriveContents, image: Bitmap): Task<Void> {
        Log.i(TAG, "New contents created.")
        // Get an output stream for the contents.
        val outputStream = driveContents.outputStream
        // Write the bitmap data from it.
        val bitmapStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream)
        try {
            outputStream.write(bitmapStream.toByteArray())
        } catch (e: IOException) {
            Log.w(TAG, "Unable to write file contents.", e)
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        val metadataChangeSet = MetadataChangeSet.Builder()
                .setMimeType("image/jpeg")
                .setTitle("Android Photo.png")
                .build()
        // Set up options to configure and display the create file activity.
        val createFileActivityOptions = CreateFileActivityOptions.Builder()
                .setInitialMetadata(metadataChangeSet)
                .setInitialDriveContents(driveContents)
                .build()

        return driveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        { task ->
                            startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0)
                            null
                        })
    }

    fun buildGoogleSignInClient(): GoogleSignInClient {

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }

    private fun signIn() {
        Log.i(TAG, "Start sign in")
        googleSignInClient = buildGoogleSignInClient()
        startActivityForResult(googleSignInClient!!.signInIntent, REQUEST_CODE_SIGN_IN)
//        googleSignInClient!!.signOut()
    }

}