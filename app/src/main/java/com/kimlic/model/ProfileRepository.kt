package com.kimlic.model

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Handler
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncServise
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs
import com.kimlic.utils.mappers.BitmapToByteArrayMapper
import org.spongycastle.util.encoders.Base64
import java.io.*

class ProfileRepository private constructor() {

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = ProfileRepository()
    }

    // Companion

    companion object {
        val instance: ProfileRepository by lazy { Holder.INSTANCE }
    }

    // Variables

    private var db: KimlicDB
    private var userDao: UserDao
    private var contactDao: ContactDao
    private var documentDao: DocumentDao
    private var addressDao: AddressDao
    private var photoDao: PhotoDao

    private var googleSignInAccount: GoogleSignInAccount? = null
    private var context: Context

    init {
        db = KimlicDB.getInstance()!!
        userDao = db.userDao()
        contactDao = db.contactDao()
        documentDao = db.documentDao()
        addressDao = db.addressDao()
        photoDao = db.photoDao()

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())

        context = KimlicApp.applicationContext()
    }

    // Public

    // User

    fun insertUser(user: User) {
        userDao.insert(user); syncDataBase()
    }

    fun getUser(accountAddress: String) = userDao.select(accountAddress)

    fun deleteUser(accountAddress: String) {
        userDao.delete(accountAddress); syncDataBase()
    }

    fun userLive(accountAddress: String): LiveData<User> = userDao.selectLive(accountAddress)

    fun addUserPhoto(accountAddress: String, fileName: String, data: ByteArray) {
        savePhoto_(fileName = fileName, data = data) // Add this name to user profile
        savePhoto_(fileName = "preview_" + fileName, data = cropedPreviewInByteArray(data)) //  save portrait preview in base64

        val user = db.userDao().select(accountAddress = accountAddress)
        user.portraitFile = fileName
        user.portraitPreviewFile = "preview_" + fileName
        db.userDao().update(user = user)
        syncDataBase()
    }


    fun addUserName(accountAddress: String, firstName: String, lastName: String) {
        val user = db.userDao().select(accountAddress = accountAddress)
        user.firstName = firstName
        user.lastName = lastName
        db.userDao().update(user)
        syncDataBase()
    }

    // Address

    fun addUserAddress_(accountAddress: String, value: String, fileName: String, data: ByteArray) {
        val userId = userDao.select(accountAddress = accountAddress).id
        val address = Address(userId = userId, value = value)
        val addressId = addressDao.insert(address)
        val addressPhoto = Photo(addressId = addressId, type = "address", file = fileName)


        savePhoto_(fileName, data); syncPhoto(fileName)

    }

    fun addressUpdate(address: Address) {
        addressDao.update(address); syncDataBase()
    }

    fun addressLive(accountAddress: String) = addressDao.selectLive(accountAddress = accountAddress)

    fun address(accountAddress: String) = addressDao.select(accountAddress = accountAddress)

    fun addressDelete(addressId: Long) {
        addressDao.delete(addressId); syncDataBase()
    }

// Contacts

    fun userContactsLive(accountAddress: String): LiveData<List<Contact>> = contactDao.selectLive(accountAddress = accountAddress)

    fun contactAdd(accountAddress: String, contact: Contact) {
        val user = db.userDao().select(accountAddress)
        val userId = user.id
        contact.userId = userId
        db.contactDao().insert(contact)
        syncDataBase()
    }

    fun contactDelete(accountAddress: String, contactType: String) = { contactDao.delete(accountAddress, contactType); syncDataBase() }

// Document

//    fun documentAdd(accountAddress: String, document: Document): Long {
//        val user = db.userDao().select(accountAddress = accountAddress)
//        val userId = user.id
//        document.userId = userId
//        val id = db.documentDao().insert(document)
//        syncDataBase()
//        return id
//    }

    fun documentsLive(accountAddress: String) = documentDao.selectLive(accountAddress = accountAddress)

    fun documentDelete(documentId: Long) = { documentDao.delete(id = documentId); syncDataBase() }

    // new
    fun addDocument(accountAddres: String, documentType: String, portraitName: String, portraitData: ByteArray, frontName: String, frontData: ByteArray, backName: String, backData: ByteArray) {
        val userId = userDao.select(accountAddress = accountAddres).id
        val documentId = documentDao.insert(document = Document(type = documentType, userId = userId))

        savePhoto_(portraitName, portraitData)
        savePhoto_(frontName, frontData)
        savePhoto_(backName, backData)

        val r = photoDao.insert(photos =
        arrayOf(
                Photo(documentId = documentId, file = portraitName, type = "portrait"),
                Photo(documentId = documentId, file = frontName, type = "front"),
                Photo(documentId = documentId, file = backName, type = "back")).asList())
    }

// Photo

    fun userDocumentPhotos(accountAddress: String, documentType: String) = photoDao.selectUserPhotosByDocument(accountAddress = accountAddress, documentType = documentType)

    fun userAddressPhoto(accountAddress: String) = photoDao.selectUserAddresPhoto(accountAddress = accountAddress)

    //fun addDocumentPhoto(vararg photos: Photo) = { photoDao.insert(photos = photos.asList()); syncDataBase() }

// Private

    private fun syncDataBase() {
        googleSignInAccount.let {
            Handler().postDelayed({
                //               if (Prefs.isUserGoogleSigned)
                //SyncServise.getInstance().updateDataBase(dataBaseName = "kimlic.db")
                SyncServise.getInstance().createOrUpdateDataBaseFileInFolder(folderName = Prefs.currentAccountAddress, appFolder = false)
            }, 1000)
        }

//        if (googleSignInAccount != null)
//            Handler().postDelayed({
//                if (Prefs.isUserGoogleSigned)
//                    SyncServise.getInstance().updateDataBase(dataBaseName = "kimlic.db")
//            }, 1000)
    }

    private fun syncPhoto(fileName: String) {
        googleSignInAccount.let {
            //SyncServise.getInstance().updateFileInFolder(fileName = fileName)
            SyncServise.getInstance().createOrUpdateFileInFolder(folderName = Prefs.currentAccountAddress, fileName = fileName, appFolder = false)
        }
    }

    // new Implementation


    // Ptivate helpers

    private fun readFromFile(context: Context, fileName: String): ByteArray {
//        try {
//            val inputStream = context.openFileInput(fileName)
//
//            inputStream.let {
//                val inputStreamReader = InputStreamReader(inputStream)
//                val bufferedReader = BufferedReader(inputStreamReader)
//
//                while ( bufferedReader.readLine()) ) {
//                    stringBuilder.append(receiveString);
//                }
//
//            }
//
//        }

        return File(fileName).inputStream().readBytes()
    }

    fun savePhoto_(fileName: String, data: ByteArray) {
        val data64 = Base64.toBase64String(data)
        writeToFile(context, fileName, data64)
        syncPhoto(fileName)
    }

    // Writes file to files directory
    private fun writeToFile(context: Context, fileName: String, data: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("TAG", "FileWriteFailed- " + e.toString())
        }
    }

    // Bitmap transformations

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int, angel: Float, isNecessaryToKeepOrig: Boolean): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        matrix.postRotate(angel)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)

        if (!isNecessaryToKeepOrig) {
            bm.recycle()
        }
        return resizedBitmap
    }

    private fun cropedPreviewInByteArray(data: ByteArray): ByteArray {
        val originalBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val resizedBitmap = getResizedBitmap(originalBitmap, 1024, 768, -90f, true)
        val width = resizedBitmap.width
        val height = resizedBitmap.height
        val cropedBitmap = Bitmap.createBitmap(resizedBitmap, (0.15 * width).toInt(), (0.12 * height).toInt(), (0.75 * width).toInt(), (0.7 * height).toInt())
        val cropedPrevireByteArray = BitmapToByteArrayMapper().transform(cropedBitmap)
        return cropedPrevireByteArray
    }

}