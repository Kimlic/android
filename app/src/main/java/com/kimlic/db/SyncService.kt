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
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SyncService private constructor(val context: Context) {

    // Constants

    private val appFolder: Boolean = false

    // Variables

    private val TAG = this::class.java.simpleName
    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account
    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.


    // Companion

    companion object {

        val MIME_TYPE_DATABASE: String = "application/db"
        val PHOTO_DESCRIPTION: String = "photo"
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

        fun getInstance(context: Context = KimlicApp.applicationContext()): SyncService = SyncService(context = context)
    }

    // Init

    init {
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        mDriveResourceClient = Drive.getDriveResourceClient(context, mGoogleSignInAccount!!)
    }

    // Public

    fun backupDatabase(accountAddress: String, dataBaseName: String, onSuccess: () -> Unit) {
        val db = KimlicApp.applicationContext().getDatabasePath(dataBaseName).toString()
        backupFile(accountAddress = accountAddress, filePath = db, fileDescription = DATABASE_DECRIPTION, onSuccess = onSuccess)
    }

    fun backupFile(accountAddress: String, filePath: String, fileDescription: String = PHOTO_DESCRIPTION, onSuccess: () -> Unit): Task<DriveFolder> {
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val rootFolder = getRootFolder()
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask {
                        if (it.getResult().count == 0) {
                            createFolderInFolder(parent = rootFolder.getResult(), folderName = accountAddress)
                            backupFile(accountAddress = accountAddress, filePath = filePath, fileDescription = fileDescription, onSuccess = {})
                        }
                        updateFile(filePath = filePath, driveFolder = it.result[0].driveId.asDriveFolder(), fileDescription = fileDescription)
                    }.addOnSuccessListener {
                        Log.d("TAG", "ON success in backupFile")
                        onSuccess()
                    }
        }
        return rootFolder
    }

    fun backupAllFiles(accountAddress: String) {
        val rootFilesDir = File(context.filesDir.toString())
        val files = rootFilesDir.listFiles()

        files.filter { !it.isDirectory }.forEach {
            Log.d("TAGTASK", " filtered.filepath = " + it.toString())
            backupFile(accountAddress = accountAddress, filePath = it.toString(), onSuccess = {})
        }


    }

    fun retrievePhotos(accountAddress: String) = retrieveFiles(accountAddress)

    fun deleteFile(rootFolderName: String, fileName: String, mimeType: String) {
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()
        val rootFolder = getRootFolder()
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask { mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuery) }.addOnSuccessListener {}
                    .continueWithTask { deleteFileByDriveId(it.result[0].driveId.asDriveFile()) }
        }
    }

    fun createAccountFolder(accountAddress: String) {
        val rootFolder = getRootFolder()
        rootFolder.continueWithTask {
            val metadataChangeSet = MetadataChangeSet.Builder().setTitle(accountAddress).setMimeType(DriveFolder.MIME_TYPE).build()

            mDriveResourceClient!!.createFolder(rootFolder.result, metadataChangeSet)
        }
    }

    // Private

    private fun retrieveFiles(accountAddress: String) {//}, appFolder: Boolean) {
        val rootFolder = getRootFolder()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).build()

        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuery)
                    }.addOnSuccessListener {
                        it.forEach {
                            Log.d(TAG, "files in folder description ${it.description}")
                            if (it.description == PHOTO_DESCRIPTION) saveFileToDisc(it.title, it.driveId.asDriveFile())
                        }
                    }.addOnSuccessListener { }
        }
    }

    fun retrieveDataBase(accountAddress: String, dataBaseName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val rootFolder = getRootFolder()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()

        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.result, backupFolderQuery)
                    .addOnFailureListener { onError() }
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuery)
                    }.addOnSuccessListener {

                        if (it.count == 0) {
                            Log.d(TAG, "found folders = ${it.count}")
                            onError(); return@addOnSuccessListener
                        }
                        it.forEach {
                            Log.d(TAG, "files in folder description ${it.description}")
                            if (it.description.equals(DATABASE_DECRIPTION)) saveDataBaseToDisc(it.title, it.driveId.asDriveFile(), onSuccess)
                        }
                    }

        }
    }

    private fun saveDataBaseToDisc(dataBaseName: String, driveFile: DriveFile, onSuccess: () -> Unit) {
        val dataBasePath = context.getDatabasePath(dataBaseName).toString()
        val openFileTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_READ_ONLY)
        openFileTask
                .addOnSuccessListener {
                    val inputStream = it.inputStream
                    val byteArray = IOUtils.toByteArray(inputStream)
                    val file = File(dataBasePath)
                    file.outputStream().write(byteArray)
                }
                .addOnSuccessListener { Log.d(TAG, "Database is restored successfully"); onSuccess() }
                .addOnFailureListener {}
    }


    private fun saveFileToDisc(fileName: String, driveFile: DriveFile) {
        val openFileTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_READ_ONLY)
        openFileTask
                .addOnSuccessListener {
                    val tempFileName = fileName // + temp
                    val inputStream = it.inputStream
                    val byteArray = IOUtils.toByteArray(inputStream)
                    val file = File(context.filesDir.toString(), tempFileName)
                    file.outputStream().write(byteArray)
                }
                .addOnSuccessListener { Log.d(TAG, "files are restored successfully!") }
                .addOnFailureListener {}
    }

    private fun updateFile(filePath: String, driveFolder: DriveFolder, fileDescription: String): Task<MetadataBuffer> {
        val fileName = filePath.split("/").last()
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val queryTasks = mDriveResourceClient!!.queryChildren(driveFolder, query)
        return queryTasks
                .addOnSuccessListener { metadataBuffer ->
                    when (metadataBuffer.count) {
                        0 -> createFileInFolder(filePath = filePath, parentFolder = driveFolder, fileDescription = fileDescription)
                        1 -> rewriteFile(filePath = filePath, driveFile = metadataBuffer.elementAt(0).driveId.asDriveFile())
                    }
                }
                .addOnSuccessListener {}
                .addOnFailureListener {}
    }

    private fun createFileInFolder(filePath: String, parentFolder: DriveFolder, fileDescription: String) {
        val fileName = filePath.split("/").last()
        val createContentTasks = mDriveResourceClient!!.createContents()
        createContentTasks
                .continueWithTask {
                    val driveContents = createContentTasks.result
                    try {
                        IOUtils.copyStream(FileInputStream(File(filePath)), driveContents.outputStream)
                    } catch (e: IOException) {
                        Log.e(TAG, "Error!!!" + e.toString())
                    }
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle(fileName).setMimeType(MIME_TYPE_DATABASE).setDescription(fileDescription).build()

                    mDriveResourceClient!!.createFile(parentFolder, metadataChangeSet, driveContents)
                }.addOnSuccessListener {
                    Log.i(TAG, "File is writen")
                }
    }

    private fun rewriteFile(filePath: String, driveFile: DriveFile) {
        val openTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        openTask
                .continueWithTask {
                    val driveContents = it.result
                    try {
                        IOUtils.copyStream(FileInputStream(File(filePath)), driveContents.outputStream)
                    } catch (e: IOException) {
                        Log.e(TAG, "rewrite exception" + e.toString())
                    }
                    mDriveResourceClient!!.commitContents(driveContents, null)
                }
                .addOnSuccessListener { Log.i(TAG, "File is rewrited") }
                .addOnFailureListener { }
    }

    // Private helpers

    private fun getRootFolder() = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder

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
        val driveResourse_ = mDriveResourceClient!!.delete(driveResource)
        driveResourse_
                .addOnCompleteListener {}
                .addOnFailureListener {}
        return driveResourse_
    }
}