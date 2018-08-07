package com.kimlic.db

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.Task
import com.kimlic.KimlicApp
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SyncServise private constructor(val context: Context) {

    // Variables

    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account
    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.

    // Companioin

    companion object {

        val MYME_TYPE_IMAGE: String = "image/jpeg"
        val MYME_TYPE_DATABASE: String = "application/db"

        fun signIn(activity: AppCompatActivity, requestCode: Int) {
            val requiredScopes = HashSet<Scope>(2)
            requiredScopes.add(Drive.SCOPE_FILE)
            requiredScopes.add(Drive.SCOPE_APPFOLDER)

            val mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
            if (mGoogleSignInAccount != null && mGoogleSignInAccount.grantedScopes.containsAll(requiredScopes)) {
            } else {
                val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(activity, signInOptions)
                activity.startActivityForResult(googleSignInClient.signInIntent, requestCode)
            }
        }

        fun signOut(activity: AppCompatActivity) {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER).build()
            GoogleSignIn.getClient(activity, googleSignInOptions).signOut()
        }

        fun getInstance(context: Context = KimlicApp.applicationContext()): SyncServise = SyncServise(context)
    }

    // Init

    init {
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        mDriveResourceClient = Drive.getDriveResourceClient(context, mGoogleSignInAccount!!)
    }

    // Public

    fun backupDatabase(accountAddress: String, dataBaseName: String) {
        val databaseFilePath = KimlicApp.applicationContext().getDatabasePath(dataBaseName).toString()
        backupFile(rootFolderName = accountAddress, filePath = databaseFilePath, appFolder = false, mimeType = MYME_TYPE_DATABASE)
    }

    fun backupFile(rootFolderName: String, filePath: String, mimeType: String, appFolder: Boolean) {
        val fileName = filePath.split("/").last()

        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()
        val rootFolder = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask {
                        if (it.getResult().count == 0) {
                            createFolderInFolder(parent = rootFolder.getResult(), folderName = rootFolderName)
                            backupFile(rootFolderName = rootFolderName, filePath = filePath, appFolder = appFolder, mimeType = mimeType)
                        } else
                            updateFile(filePath = fileName, driveFolder = it.result[0].driveId.asDriveFolder(), mimeType = mimeType)

                        deleteFolderAsDriveResource(1 as DriveResource)
                    }
        }
    }



    // Private

    private fun updateFile(filePath: String, driveFolder: DriveFolder, mimeType: String = MYME_TYPE_IMAGE) {
        val fileName = filePath.split("/").last()
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val queryTasks = mDriveResourceClient!!.queryChildren(driveFolder, query)
        queryTasks
                .addOnSuccessListener { metadataBuffer ->
                    when (metadataBuffer.count) {
                        0 -> createFileInFolder(filePath = filePath, parentFolder = driveFolder, mimeType = mimeType)
                        1 -> rewriteFile(filePath = filePath, driveFile = metadataBuffer.elementAt(0).driveId.asDriveFile())
                    }
                }
                .addOnSuccessListener { }
                .addOnFailureListener { }
    }

    private fun createFileInFolder(filePath: String, parentFolder: DriveFolder, mimeType: String) {
        val fileName = filePath.split("/").last()
        val createContentTasks = mDriveResourceClient!!.createContents()
        createContentTasks
                .continueWithTask {
                    val driveContents = createContentTasks.result
                    try {
                        val file = File(filePath)
                        val fis = FileInputStream(file)
                        val outputStream_ = driveContents.outputStream
                        val buffer = ByteArray(1024)
                        while (fis.read(buffer) > 0) outputStream_.write(buffer, 0, fis.read(buffer))
                    } catch (e: IOException) {
                    }
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle(fileName).setMimeType(mimeType).build()

                    mDriveResourceClient!!.createFile(parentFolder, metadataChangeSet, driveContents)
                }.addOnSuccessListener { Log.d("TAGTASK", "file created!!!")}
    }

    private fun rewriteFile(filePath: String, driveFile: DriveFile) {
        val openTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        openTask
                .continueWithTask {
                    val driveContents = it.result
                    try {
                        val dbFile = File(filePath)
                        val fis = FileInputStream(dbFile)
                        val outputStream_ = driveContents.outputStream
                        val buffer = ByteArray(1024)

                        while (fis.read(buffer) > 0) outputStream_.write(buffer, 0, fis.read(buffer))
                    } catch (e: IOException) {
                    }
                    mDriveResourceClient!!.commitContents(driveContents, null)
                }
                .addOnSuccessListener { Log.d("TAGTASK", "rewrite FILE in folder  SUCCESS") }
                .addOnFailureListener { Log.d("TAGTASK", "rewrite FILE ERROROROROR") }
    }

    // Private helpers

    private fun createFolderInFolder(parent: DriveFolder, folderName: String): Task<DriveFolder> {
        val changeSet = MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(false)
                .setDescription("Account address")
                .build()
        return mDriveResourceClient!!
                .createFolder(parent, changeSet)
                .addOnSuccessListener {
                    Log.d("TAGTASK", " creata folder in folder success created")
                }
                .addOnFailureListener {}
    }

    private fun deleteFile(driveFile: DriveFile) {
        val driveResource = mDriveResourceClient!!.delete(driveFile)
        driveResource
                .addOnCompleteListener { Log.d("TAGTASK", "inFileDeleted!!!!!!") }
                .addOnFailureListener {}
    }

    private fun deleteFolderAsDriveResource(driveResource: DriveResource): Task<Void> {
        Log.d("TAGTASK", "Delete folder as drive resource")
        val driveResource_ = mDriveResourceClient!!.delete(driveResource)
        driveResource_
                .addOnCompleteListener {}
                .addOnFailureListener {}
        return driveResource_
    }
}

