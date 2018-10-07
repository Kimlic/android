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
import com.kimlic.BuildConfig
import com.kimlic.KimlicApp
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class SyncService private constructor(val context: Context) {

    // Constants

    private val appFolder: Boolean = BuildConfig.RECOVERY_FOLDER

    // Variables

    private val TAG = this::class.java.simpleName!!
    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account
    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.

    // Companion

    companion object {

        const val MIME_TYPE_DATABASE: String = "application/db"
        const val PHOTO_DESCRIPTION: String = "photo" // file description
        const val DATABASE_DESCRIPTION: String = "database" // database description

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

    fun backupDatabase(accountAddress: String, dataBaseName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val db = KimlicApp.applicationContext().getDatabasePath(dataBaseName).toString()
        backupFile(accountAddress = accountAddress, filePath = db, fileDescription = DATABASE_DESCRIPTION, onSuccess = onSuccess, onError = onError)
    }

    fun backupFile(accountAddress: String, filePath: String, fileDescription: String, onSuccess: () -> Unit, onError: () -> Unit): Task<DriveFolder> {
        val rootFolder = getRootFolder()
        val backupFolderQuery = getBackupFolder(accountAddress)

        rootFolder.continueWithTask { _ ->
            mDriveResourceClient!!
                    .queryChildren(rootFolder.result!!, backupFolderQuery)
                    .continueWithTask { it ->
                        if (it.result!!.count == 0) {
                            createFolderInFolder(parent = rootFolder.result!!, folderName = accountAddress)
                                    .addOnSuccessListener {
                                        backupFile(accountAddress = accountAddress, filePath = filePath, fileDescription = fileDescription, onSuccess = { onSuccess() }, onError = onError)
                                    }
                        }
                        updateFile(filePath = filePath, driveFolder = it.result!![0].driveId.asDriveFolder(), fileDescription = fileDescription)
                    }.addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener { onError() }
        }
        return rootFolder
    }

    fun retrieveFile(accountAddress: String, fileName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val rootFolder = getRootFolder()
        val backupFolderQuery = getBackupFolder(accountAddress)
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()

        rootFolder.continueWithTask { _ ->
            mDriveResourceClient!!.queryChildren(rootFolder.result!!, backupFolderQuery)
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result!![0].driveId.asDriveFolder(), fileQuery)
                    }.continueWithTask {
                        val n = it.result!![0].title
                        saveFileToDisc(it.result!![0].title, it.result!![0].driveId.asDriveFile())
                    }
                    .addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener {
                        onError()
                    }
        }
    }

    fun retrieveDataBase(accountAddress: String, dataBaseName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val rootFolder = getRootFolder()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()
        rootFolder.continueWithTask { _ ->
            mDriveResourceClient!!
                    .queryChildren(rootFolder.result!!, backupFolderQuery)
                    .addOnFailureListener { onError() }
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result!![0].driveId.asDriveFolder(), fileQuery)
                    }.addOnSuccessListener { it ->
                        if (it.count == 0) {
                            onError(); return@addOnSuccessListener
                        }
                        it.forEach {
                            if (it.description == DATABASE_DESCRIPTION) saveDataBaseToDisc(it.title, it.driveId.asDriveFile(), onSuccess)
                        }
                    }.addOnFailureListener {
                        onError()
                    }
        }
    }

    // Private

    fun deleteFile(rootFolderName: String, fileName: String, mimeType: String) {
        val rootFolder = getRootFolder()
        val backupFolderQuery = getBackupFolder(rootFolderName)
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()

        rootFolder.continueWithTask { _ ->
            mDriveResourceClient!!.queryChildren(rootFolder.result!!, backupFolderQuery)
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result!![0].driveId.asDriveFolder(), fileQuery)
                    }.continueWithTask {
                        deleteFileByDriveId(it.result!![0].driveId.asDriveFile())
                    }
        }
    }

    fun deleteFolder(accountAddress: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val rootFolder = getRootFolder()
        val removeFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()

        rootFolder.continueWithTask {
            mDriveResourceClient!!.queryChildren(rootFolder.result!!, removeFolderQuery)
        }.continueWithTask {
            deleteFolderAsDriveResource(it.result!![0].driveId.asDriveResource())
        }
                .addOnSuccessListener { onSuccess() }
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
                .addOnSuccessListener { Log.d("TAGRECOVERY", "Database is restored successfully"); onSuccess() }
                .addOnFailureListener {}
    }

    private fun saveFileToDisc(fileName: String, driveFile: DriveFile): Task<DriveContents> {
        val openFileTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_READ_ONLY)
        return openFileTask
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
                        IOUtils.copyStream(FileInputStream(File(filePath)), driveContents!!.outputStream)
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
                        IOUtils.copyStream(FileInputStream(File(filePath)), driveContents!!.outputStream)
                    } catch (e: IOException) {
                        Log.e("TAGBACLUP", "rewrite exception" + e.toString())
                    }
                    mDriveResourceClient!!.commitContents(driveContents!!, null)
                }
                .addOnSuccessListener { Log.d("TAGBACLUP", "File is rewrited") }
                .addOnFailureListener { }
    }

    // Private helpers

    private fun getRootFolder() = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder

    private fun getBackupFolder(rootFolderName: String) = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()

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
        driveResource
                .addOnCompleteListener { }
                .addOnFailureListener { }
        return driveResource
    }

    private fun deleteFolderAsDriveResource(driveResource: DriveResource): Task<Void> {
        val driveResourse_ = mDriveResourceClient!!.delete(driveResource)
        driveResourse_
                .addOnCompleteListener {}
                .addOnFailureListener {}
        return driveResourse_
    }
}