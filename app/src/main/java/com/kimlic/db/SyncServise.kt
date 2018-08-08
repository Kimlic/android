package com.kimlic.db

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.util.IOUtils
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.Task
import com.kimlic.KimlicApp
import java.io.*


class SyncServise private constructor(val context: Context) {

    // Variables

    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account
    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.

    // Companioin

    companion object {

        val MIME_TYPE_IMAGE: String = "image/jpeg"
        val MIME_TYPE_DATABASE: String = "application/db"
        val FILE_DESCRIPTION: String = "file"
        val DATABASE_DECRIPTION: String = "database"

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
        val fileDescription = "database"
        backupFile(rootFolderName = accountAddress, filePath = databaseFilePath, appFolder = false, mimeType = MIME_TYPE_DATABASE, fileDescription = fileDescription)
    }

    fun backupFile(rootFolderName: String, filePath: String, mimeType: String, appFolder: Boolean, fileDescription: String) {
        //val fileName = filePath.split("/").last()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()
        val rootFolder = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask {
                        if (it.getResult().count == 0) {
                            createFolderInFolder(parent = rootFolder.getResult(), folderName = rootFolderName)
                            backupFile(rootFolderName = rootFolderName, filePath = filePath, appFolder = appFolder, mimeType = mimeType, fileDescription = fileDescription)
                        } else
                            updateFile(filePath = filePath, driveFolder = it.result[0].driveId.asDriveFolder(), mimeType = mimeType, fileDescription = fileDescription)

                        deleteFolderAsDriveResource(1 as DriveResource)
                    }
        }
    }

    fun retriveFile(rootFolderName: String, fileName: String, mimeType: String, appFolder: Boolean, fileDescription: String, database: Boolean = false) {
        val fileQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).build()
        val backupFolderQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()
        val rootFolder = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder

        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuerry)
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuerry)
                    }.addOnSuccessListener {
                        it.forEach {
                            if (it.description.equals(fileDescription)) saveFileToDisck(it.title, it.driveId.asDriveFile())
                        }
                    }
        }
    }

    fun retriveDatabase(accountAddress: String, databaseName: String, fileName: String, mimeType: String, appFolder: Boolean) {
        val databasePath = context.getDatabasePath("kimlic.db")
        Log.d("TAGTASK", "databasePath = " + databasePath)
        retriveFile(rootFolderName = accountAddress, fileName = fileName, mimeType = MIME_TYPE_DATABASE, appFolder = appFolder, fileDescription = DATABASE_DECRIPTION)
    }

    fun deleteFile(rootFolderName: String, fileName: String, mimeType: String, appFolder: Boolean) {
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()
        val rootFolder = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask { mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuery) }.addOnSuccessListener {}
                    .continueWithTask { deleteFileByDriveId(it.result[0].driveId.asDriveFile()) }
        }
    }

    // Private

    private fun saveFileToDisck(fileName: String, driveFile: DriveFile) {
        val openFileTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_READ_ONLY)// Start open file
        openFileTask
                .addOnSuccessListener {
                    val tempFileName = fileName // + temp
                    val inputStream = it.inputStream
                    val byteArray = IOUtils.toByteArray(inputStream)
                    val file = File(context.filesDir.toString(), tempFileName)
                    file.outputStream().write(byteArray)
                }
                .addOnSuccessListener { Log.d("TAGTASK", "on success") }
                .addOnFailureListener { Log.d("TAGTASK", "on failru - " + it.toString()) }
    }

    private fun updateFile(filePath: String, driveFolder: DriveFolder, mimeType: String = MIME_TYPE_IMAGE, fileDescription: String) {
        val fileName = filePath.split("/").last()
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val queryTasks = mDriveResourceClient!!.queryChildren(driveFolder, query)
        queryTasks
                .addOnSuccessListener { metadataBuffer ->
                    when (metadataBuffer.count) {
                        0 -> createFileInFolder(filePath = filePath, parentFolder = driveFolder, mimeType = mimeType, fileDescription = fileDescription)
                        1 -> rewriteFile(filePath = filePath, driveFile = metadataBuffer.elementAt(0).driveId.asDriveFile())
                    }
                }
                .addOnSuccessListener { }
                .addOnFailureListener { }
    }

    private fun createFileInFolder(filePath: String, parentFolder: DriveFolder, mimeType: String, fileDescription: String) {
        val fileName = filePath.split("/").last()
        val createContentTasks = mDriveResourceClient!!.createContents()
        createContentTasks
                .continueWithTask {
                    val driveContents = createContentTasks.result
                    try {
                        val file = File(filePath)
                        val fis = FileInputStream(file)
                        val outputStream_ = driveContents.outputStream
                        IOUtils.copyStream(fis, outputStream_)
                    } catch (e: IOException) {
                        Log.d("TAGTASK", "Error!!!" + e.toString())
                    }
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle(fileName).setMimeType(mimeType).setDescription(fileDescription).build()

                    mDriveResourceClient!!.createFile(parentFolder, metadataChangeSet, driveContents)
                }.addOnSuccessListener {
                    Log.d("TAGTASK", "FileIs writen")
                }
    }

    private fun rewriteFile(filePath: String, driveFile: DriveFile) {
        val openTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        openTask
                .continueWithTask {
                    val driveContents = it.result
                    try {
                        val file = File(filePath)
                        val fis = FileInputStream(file)
                        val outputStream_ = driveContents.outputStream
                        IOUtils.copyStream(fis, outputStream_)
                    } catch (e: IOException) {
                        Log.d("TAGTASK", "rewrite exception" + e.toString())
                    }
                    mDriveResourceClient!!.commitContents(driveContents, null)
                }
                .addOnSuccessListener { }
                .addOnFailureListener { }
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
                .addOnSuccessListener {}
                .addOnFailureListener {}
    }

    private fun deleteFileByDriveId(driveFile: DriveFile): Task<Void> {
        val driveResource = mDriveResourceClient!!.delete(driveFile)
        return driveResource
                .addOnCompleteListener { }
                .addOnFailureListener { }
    }

    private fun deleteFolderAsDriveResource(driveResource: DriveResource): Task<Void> {
        val driveResource_ = mDriveResourceClient!!.delete(driveResource)
        driveResource_
                .addOnCompleteListener {}
                .addOnFailureListener {}
        return driveResource_
    }
}

