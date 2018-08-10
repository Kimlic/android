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
    private val TAG = this::class.java.simpleName
    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account
    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.

    // Companioin

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

        fun getInstance(context: Context = KimlicApp.applicationContext()): SyncServise = SyncServise(context)
    }

    // Init

    init {
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        mDriveResourceClient = Drive.getDriveResourceClient(context, mGoogleSignInAccount!!)
    }

    // Public

    fun backupDatabase(accountAddress: String, dataBaseName: String, appFolder: Boolean, onSuccess: () -> Unit) {
        val db = KimlicApp.applicationContext().getDatabasePath(dataBaseName).toString()
        backupFile(accountAddress = accountAddress, filePath = db, appFolder = appFolder, fileDescription = DATABASE_DECRIPTION)
    }

    fun backupFile(accountAddress: String, filePath: String, appFolder: Boolean, fileDescription: String = PHOTO_DESCRIPTION): Task<DriveFolder> {
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val rootFolder = getRootFolder(appFolder)
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask {
                        if (it.getResult().count == 0) {
                            createFolderInFolder(parent = rootFolder.getResult(), folderName = accountAddress)
                            backupFile(accountAddress = accountAddress, filePath = filePath, appFolder = appFolder, fileDescription = fileDescription)
                        }
                        updateFile(filePath = filePath, driveFolder = it.result[0].driveId.asDriveFolder(), fileDescription = fileDescription)
                    }
        }
        return rootFolder
    }

    fun retrivePhotos(accountAddress: String, appFolder: Boolean) = retriveFiles(accountAddress, appFolder)

    fun deleteFile(rootFolderName: String, fileName: String, mimeType: String, appFolder: Boolean) {
        val fileQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType)).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, rootFolderName)).build()
        val rootFolder = getRootFolder(appFolder)
        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask { mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuery) }.addOnSuccessListener {}
                    .continueWithTask { deleteFileByDriveId(it.result[0].driveId.asDriveFile()) }
        }
    }

    fun createAccountFolder(accountAddress: String, appFolder: Boolean) {
        val rootFolder = getRootFolder(appFolder)
        rootFolder.continueWithTask {
            val metadataChangeSet = MetadataChangeSet.Builder().setTitle(accountAddress).setMimeType(DriveFolder.MIME_TYPE).build()

            mDriveResourceClient!!.createFolder(rootFolder.result, metadataChangeSet)
        }
    }

    // Private

    private fun retriveFiles(accountAddress: String, appFolder: Boolean) {
        val rootFolder = getRootFolder(appFolder)
        val backupFolderQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val fileQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).build()

        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuerry)
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuerry)
                    }.addOnSuccessListener {
                        it.forEach {
                            Log.d(TAG, "files in folder description ${it.description}")
                            if (it.description.equals(PHOTO_DESCRIPTION)) saveFileToDisc(it.title, it.driveId.asDriveFile())
                        }
                    }
        }
    }

    fun retriveDataBase(accountAddress: String, dataBaseName: String, appFolder: Boolean, onSuccess: () -> Unit) {
        val rootFolder = getRootFolder(appFolder)
        val backupFolderQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, accountAddress)).build()
        val fileQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_DATABASE)).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()

        rootFolder.continueWithTask {
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuerry)
                    .continueWithTask {
                        mDriveResourceClient!!.queryChildren(it.result[0].driveId.asDriveFolder(), fileQuerry)
                    }.addOnSuccessListener {

                        it.forEach {
                            Log.d(TAG, "files in folder description ${it.description}")
                            if (it.description.equals(DATABASE_DECRIPTION)) saveDataBaseToDisc(it.title, it.driveId.asDriveFile(), onSuccess)
                        }
                    }
        }
    }

    private fun saveDataBaseToDisc(dataBaseName: String, driveFile: DriveFile, onSuccess: () -> Unit) {
        val dataBasePath = context.getDatabasePath(dataBaseName).toString()
        Log.d("TAGTASK", "database path = " + dataBasePath)
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
                .addOnSuccessListener { Log.d(TAG, "files are restored successafully!") }
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

    private fun getRootFolder(appFolder: Boolean) = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder

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

