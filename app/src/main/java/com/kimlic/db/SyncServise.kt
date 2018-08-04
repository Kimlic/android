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
import com.google.android.gms.tasks.Tasks
import com.kimlic.KimlicApp
import com.kimlic.preferences.Prefs
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStreamWriter

class SyncServise private constructor(val context: Context) {

    // Variables

    private var mGoogleSignInAccount: GoogleSignInAccount? = null // SignIn account
    private var mDriveResourceClient: DriveResourceClient? = null // Handle access to Drive resources/files.

    // Companioin

    companion object {

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

    fun updateFile(fileName: String) {
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, "image/jpeg")).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val queryTasks = mDriveResourceClient!!.query(query)

        queryTasks
                .addOnSuccessListener { metadataBuffer ->
                    when (metadataBuffer.count) {
                        0 -> createFile(fileName)
                        1 -> rewriteFile(metadataBuffer.elementAt(0).driveId.asDriveFile(), fileName)
                    }
                }
                .addOnSuccessListener { }
                .addOnFailureListener { }
    }

    fun updateDataBase(dataBaseName: String) {
        val dataBasePath = context.getDatabasePath(dataBaseName).toString()
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/db")).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()
        val querryTask = mDriveResourceClient!!.query(query)

        querryTask
                .addOnSuccessListener { metadataBuffer ->
                    when (metadataBuffer.count) {
                        0 -> createDBFile(dataBaseName = dataBaseName)
                        1 -> rewriteDatabase(driveFile = metadataBuffer.elementAt(0).driveId.asDriveFile(), databasePath = dataBasePath)
                    }
                }
                .addOnFailureListener {
                    Log.d("TAG", " failru - " + it.toString())
                }
        createOrUpdateDatabaseInFolder(Prefs.currentAccountAddress)
    }

    fun createUpdateFolder(folderName: String, d: String) {
        val querryFolder = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, folderName)).build()

        mDriveResourceClient!!.query(querryFolder).addOnSuccessListener { metadataBuffer ->
            metadataBuffer.forEach { Log.d("TAG", "folder is found") }

        }
    }

//    fun deleteDatabase(dataBaseName: String) {
//        val dataBasePath = context.getDatabasePath(dataBaseName)
//        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/db")).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()
//        val querryTask = mDriveResourceClient!!.query(query)
//
//        querryTask.addOnSuccessListener { metadataBuffer ->
//            metadataBuffer.forEach { deleteFile(driveFile = it.driveId.asDriveFile()) }
//        }
//    }

    fun deleteFile(driveFile: DriveFile) {
        val driveResource = mDriveResourceClient!!.delete(driveFile)
        driveResource
                .addOnCompleteListener {}
                .addOnFailureListener {}
    }


    private fun createDBFile(dataBaseName: String) {
        val dataBasePath = context.getDatabasePath(dataBaseName).toString()

        //val appFolderTask = mDriveResourceClient!!.appFolder
        val rootFolderTask = mDriveResourceClient!!.rootFolder
        val createContentTasks = mDriveResourceClient!!.createContents()

        Tasks
                .whenAll(rootFolderTask, createContentTasks)
                //Tasks.whenAll(rootFolder, createContentTasks)
                .continueWithTask {
                    //val parent = folderTask.getResult()
                    val parent = rootFolderTask.getResult()
                    val driveContents = createContentTasks.result
                    try {
                        val dbFile = File(dataBasePath)
                        val fis = FileInputStream(dbFile)
                        val outputStream_ = driveContents.outputStream
                        val buffer = ByteArray(1024)
                        while (fis.read(buffer) > 0) outputStream_.write(buffer, 0, fis.read(buffer))

                    } catch (e: IOException) {
                    }
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle("kimlic.db").setMimeType("application/db").build()

                    mDriveResourceClient!!.createFile(parent, metadataChangeSet, driveContents)
                }
    }

    private fun createFile(fileName: String) {
        val filePath = context.fileList().toString() + "/" + fileName

        //val appFolderTask = mDriveResourceClient!!.appFolder
        val rootFolderTask = mDriveResourceClient!!.rootFolder
        val createContentTasks = mDriveResourceClient!!.createContents()

        Tasks
                .whenAll(rootFolderTask, createContentTasks)
                .continueWithTask {
                    val parent = rootFolderTask.getResult()
                    val driveContents = createContentTasks.getResult()

                    try {
                        val file = File(filePath)
                        val fis = FileInputStream(file)
                        val outputStream_ = driveContents.outputStream
                        val buffer = ByteArray(1024)
                        while (fis.read(buffer) > 0) outputStream_.write(buffer, 0, fis.read(buffer))

                    } catch (e: IOException) {
                    }
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle(fileName).setMimeType("image/jpeg").build()

                    mDriveResourceClient!!.createFile(parent, metadataChangeSet, driveContents)
                }
    }

    private fun rewriteDatabase(driveFile: DriveFile, databasePath: String) {
        val openTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        openTask
                .continueWithTask {
                    val driveContents = it.getResult()
                    try {
                        val dbFile = File(databasePath)
                        val fis = FileInputStream(dbFile)
                        val outputStream_ = driveContents.outputStream
                        val buffer = ByteArray(1024)

                        while (fis.read(buffer) > 0) outputStream_.write(buffer, 0, fis.read(buffer))
                    } catch (e: IOException) {
                    }
                    mDriveResourceClient!!.commitContents(driveContents, null)
                }
//                .addOnSuccessListener {}
//                .addOnFailureListener {}
    }

    private fun rewriteFile(driveFile: DriveFile, fileName: String) {
        val filePath = context.filesDir.toString() + "/" + fileName
        val openTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        openTask
                .continueWithTask {
                    val driveContents = it.getResult()

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
        //        .addOnSuccessListener {}
        //        .addOnFailureListener {}
    }

    private fun createFolder(folderName: String) {
        mDriveResourceClient!!
                .appFolder
                .continueWithTask {
                    val parentFolder = it.getResult()
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle(folderName).setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build()

                    mDriveResourceClient!!.createFolder(parentFolder, metadataChangeSet)
                }
    }
    // folderName = currentAccountAddress accountAddress

    private fun createOrUpdateDatabaseInFolder(folderName: String, task: Task<DriveFile>? = null) {
        val rootFolder = mDriveResourceClient!!.rootFolder

        val changeSet = MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(true)
                .build()


//        val fff = mDriveResourceClient!!
//                .createFolder(rootFolder.result, changeSet)
//                .addOnSuccessListener {}
//                .addOnFailureListener {}

        // val driveFolder = fff.getResult()

        val rootFolderQuery = mDriveResourceClient!!.rootFolder

        rootFolderQuery.continueWithTask {

            val backupFolderQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, folderName)).build()

            mDriveResourceClient!!.query(backupFolderQuerry).addOnSuccessListener { metaDataBuffer ->
                Log.d("TAGTASK", "count = " + metaDataBuffer.count)

                when (metaDataBuffer.count) {
                    0 -> {
                        Log.d("TAGTASK", "FolderNotFound and witl be created")
                        createFolderInFolder(it.result, folderName)
                    }
                    1, 2, 3, 4, 5, 6, 7, 8, 9 -> {
                        Log.d("TAGTASK", "folder found!!!")

                    }
                }
            }
        }

    }

    private fun createFileInFolder(parent: DriveFolder, folderName: String) {
        val fileTask =
                mDriveResourceClient!!
                        .createContents()
                        .continueWithTask({ task ->
                            val contents = task.getResult()
                            val outputStream = contents.getOutputStream()
                            OutputStreamWriter(outputStream).use { writer -> writer.write("Hello World!") }

                            val changeSet = MetadataChangeSet.Builder()
                                    .setTitle("New file")
                                    .setMimeType("text/plain")
                                    .setStarred(true)
                                    .build()

                            mDriveResourceClient!!.createFile(parent, changeSet, contents)
                        })

    }

    private fun createFolderInFolder(parent: DriveFolder, folderName: String) {
        val changeSet = MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(true)
                .build()

        mDriveResourceClient!!
                .createFolder(parent, changeSet)

    }
}

