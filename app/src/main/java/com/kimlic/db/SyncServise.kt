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
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SyncServise private constructor(val context: Context) {

    // Constants

    private val MYME_TYPE_IMAGE: String = "image/jpeg"
    private val MYME_TYPE_DATABASE: String = "application/db"

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

    // new wrapper on opert4ions vith file
    fun createorUpdateDataBaseInFolderWrapper(dataBAseName: String) {
        
    }

    fun updateFileInFolder(fileName: String, driveFolder: DriveFolder) {
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, "image/jpeg")).addFilter(Filters.eq(SearchableField.TITLE, fileName)).build()
        val queryTasks = mDriveResourceClient!!.queryChildren(driveFolder, query)

        queryTasks
                .addOnSuccessListener { metadataBuffer ->
                    when (metadataBuffer.count) {
                        0 -> createFileInFolder(fileName, driveFolder)
                        1 -> rewriteFile(fileName, metadataBuffer.elementAt(0).driveId.asDriveFile())

                    }
                }
                .addOnSuccessListener { }
                .addOnFailureListener { }
    }


    fun createOrUpdateFileInFolder(folderName: String, fileName: String, appFolder: Boolean = true) {
        // поиск папки с насванием аккаунта
        val backupFolderQuery = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, folderName)).build()
        // В этой папке будем искать папку для бэкапа
        val rootFolder = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder

        rootFolder.continueWithTask {
            // Found folder in rootFolder
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuery)
                    .continueWithTask {
                        if (it.getResult().count == 0) {
                            createFolderInFolder(rootFolder.getResult(), folderName)
                            createOrUpdateFileInFolder(folderName = folderName, fileName = fileName, appFolder = appFolder)
                        } else
                            updateFileInFolder(fileName = fileName, driveFolder = it.result[0].driveId.asDriveFolder())

                        deleteFolderAsDriveResource(1 as DriveResource)
                    }
        }
    }

    fun createOrUpdateDataBaseFileInFolder(folderName: String, appFolder: Boolean = true) {
        // Этот запрос ищес в корневом каталоге папу с названием, которое передано на входк функции это будет аккаунт адрес пользователя
        val backupFolderQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).addFilter(Filters.eq(SearchableField.TITLE, folderName)).build()

        val rootFolder = if (appFolder) mDriveResourceClient!!.appFolder else mDriveResourceClient!!.rootFolder
        // Это входной таск в качестве которого приходть DriverFolder
        rootFolder.continueWithTask {

            // это выходной task
            mDriveResourceClient!!
                    .queryChildren(rootFolder.getResult(), backupFolderQuerry)
                    .addOnSuccessListener { metaDataBuffer ->
                        Log.d("TAGTASK", "count of folder ${folderName} = " + metaDataBuffer.count)
                    }.continueWithTask {
                        Log.d("TAGTASK", "count in continue in folderName ${folderName} = " + it.getResult().count)
                        if (it.getResult().count == 0) {
                            Log.d("TAGTASK", "FolderNotFound and wil be created be created")
                            createFolderInFolder(rootFolder.getResult(), folderName)
                            createOrUpdateDataBaseFileInFolder(folderName)
                        } else {
                            //deleteFolderAsDriveResource(it.result[0].driveId.asDriveFolder())
                            //createDBFileInFolder(folderName, it.result[0].driveId.asDriveFolder())
                            Log.d("TAGTASK", "Foldre already exists and next tu create or upadte database file ")
                            updataDatabaseInFolder("kimlic.db", it.result[0].driveId.asDriveFolder())
                        }
                        deleteFolderAsDriveResource(1 as DriveResource)

                    }
        }
    }

    private fun updataDatabaseInFolder(dataBaseName: String, driveFolder: DriveFolder) {
        val databasePath = context.getDatabasePath(dataBaseName)

        val dbFileQuerry = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/db")).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()
        val queryTask = mDriveResourceClient!!.queryChildren(driveFolder, dbFileQuerry)

        queryTask
                .addOnSuccessListener {
                }.continueWithTask {
                    if (it.result.count == 0) {
                        createDBFileInFolder("kimlic.db", driveFolder)
                    } else {
                        rewriteDatabase("kimlic.db", it.result[0].driveId.asDriveFile())
                    }
                    deleteDatabase("stub")
                }
    }

    private fun deleteDatabase(dataBaseName: String): Task<MetadataBuffer> {
        val dataBasePath = context.getDatabasePath(dataBaseName)
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/db")).addFilter(Filters.eq(SearchableField.TITLE, dataBaseName)).build()
        val querryTask = mDriveResourceClient!!.query(query)

        querryTask.addOnSuccessListener { metadataBuffer ->
            metadataBuffer.forEach { deleteFile(driveFile = it.driveId.asDriveFile()) }
        }
        return mDriveResourceClient!!.query(query)
    }


    // calls from update dbfile in folder!
    private fun createDBFileInFolder(dataBaseName: String, parentFolder: DriveFolder): Task<DriveContents> {
        val dataBasePath = context.getDatabasePath(dataBaseName).toString()
        val createContentTasks = mDriveResourceClient!!.createContents()
        createContentTasks
                .continueWithTask {
                    val parent = parentFolder
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
        return createContentTasks
    }

    private fun createFileInFolder(fileName: String, parentFolder: DriveFolder, mimeType: String = "image/jpeg") {
        val filePath = context.fileList().toString() + "/" + fileName
        val createContentTasks = mDriveResourceClient!!.createContents()

        createContentTasks
                .continueWithTask {
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

                    mDriveResourceClient!!.createFile(parentFolder, metadataChangeSet, driveContents)
                }.addOnSuccessListener { }
    }

    private fun rewriteDatabase(databasePath: String, driveFile: DriveFile) {
        val openTask = mDriveResourceClient!!.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        openTask
                .continueWithTask {
                    Log.d("TAGTASK", "Inside rewrite database!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
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
                .addOnSuccessListener {
                    Log.d("TAGTASK", "DATABASE REWRITED SUUCESSFULLY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                }
                .addOnFailureListener {
                    Log.d("TAGTASK", "DATABASE REWRITED ERRORRRRRRRR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                }
    }


    // Function use driveId and file name from files
    private fun rewriteFile(fileName: String, driveFile: DriveFile) {
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
                .addOnSuccessListener { Log.d("TAGTASK", "rewrite FILE in folder  SUCCESS") }
                .addOnFailureListener { Log.d("TAGTASK", "rewrite FILE ERROROROROR") }
    }

    // Native function


    // private helpers

    fun deleteFile(driveFile: DriveFile) {
        val driveResource = mDriveResourceClient!!.delete(driveFile)
        driveResource
                .addOnCompleteListener { Log.d("TAGTASK", "inFileDeleted!!!!!!") }
                .addOnFailureListener {}
    }

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

    fun deleteFolderAsDriveResource(driveResource: DriveResource): Task<Void> {
        Log.d("TAGTASK", "Delete folder as drive resource")
        val driveResource_ = mDriveResourceClient!!.delete(driveResource)
        driveResource_
                .addOnCompleteListener {}
                .addOnFailureListener {}

        return driveResource_
    }

    // Unused ?

    // In future change rootFoldre for appFolder! this is task.
    fun createFolderInRootFolder(folderName: String) {
        mDriveResourceClient!!
                .rootFolder
                .continueWithTask {
                    // init
                    val parentFolder = it.getResult()// in this case it's driveFolder is root folder

                    val metadataChangeSet = MetadataChangeSet.Builder().setMimeType(DriveFolder.MIME_TYPE).setTitle(folderName).build()

                    mDriveResourceClient!!.createFolder(parentFolder, metadataChangeSet)
                }
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

    private fun createFolder(folderName: String) {
        mDriveResourceClient!!
                .appFolder
                .continueWithTask {
                    val parentFolder = it.getResult()
                    val metadataChangeSet = MetadataChangeSet.Builder().setTitle(folderName).setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build()

                    mDriveResourceClient!!.createFolder(parentFolder, metadataChangeSet)
                }
    }
}

