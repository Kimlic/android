package com.kimlic.model

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Handler
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.util.IOUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.KimlicRequest
import com.kimlic.API.SyncObject
import com.kimlic.API.VolleySingleton
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncServise
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.QuorumURL
import com.kimlic.utils.mappers.BitmapToByteArrayMapper
import org.json.JSONObject
import org.spongycastle.util.encoders.Base64
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset

class ProfileRepository private constructor() {

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = ProfileRepository()
    }

    // Companion

    companion object {
        val instance: ProfileRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Holder.INSTANCE }
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

    // Init

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
        savePhoto_(fileName = fileName, data = data)
        savePhoto_(fileName = "preview_" + fileName, data = cropedPreviewInByteArray(data))
        val user = userDao.select(accountAddress = accountAddress)
        user.portraitFile = fileName
        user.portraitPreviewFile = "preview_" + fileName
        userDao.update(user = user)
        syncDataBase()
    }

    fun addUserName(accountAddress: String, firstName: String, lastName: String) {
        val user = userDao.select(accountAddress = accountAddress)
        user.firstName = firstName
        user.lastName = lastName
        userDao.update(user)
        syncDataBase()
    }

    // Address

    fun addUserAddress(accountAddress: String, value: String, fileName: String, data: ByteArray) {
        val userId = userDao.select(accountAddress = accountAddress).id
        val address = Address(userId = userId, value = value)
        val addressId = addressDao.insert(address)
        val addressPhoto = Photo(addressId = addressId, type = "address", file = fileName)
        photoDao.insert(photos = arrayOf(addressPhoto).asList())
        syncDataBase()
        savePhoto_(fileName, data)
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
        val user = userDao.select(accountAddress)
        val userId = user.id
        contact.userId = userId
        contactDao.insert(contact)
        syncDataBase()
    }

    fun contactDelete(accountAddress: String, contactType: String) = { contactDao.delete(accountAddress, contactType); syncDataBase() }

    // Document

    fun documents(accountAddres: String) = documentDao.select(accountAddress = accountAddres)

    fun documentsLive(accountAddress: String) = documentDao.selectLive(accountAddress = accountAddress)

    fun documentDelete(documentId: Long) = { documentDao.delete(id = documentId); syncDataBase() }

    fun addDocument(accountAddres: String, documentType: String, portraitName: String, portraitData: ByteArray, frontName: String, frontData: ByteArray, backName: String, backData: ByteArray) {
        val userId = userDao.select(accountAddress = accountAddres).id
        val documentId = documentDao.insert(document = Document(type = documentType, userId = userId))

        photoDao.insert(photos =
        arrayOf(
                Photo(documentId = documentId, file = portraitName, type = "portrait"),
                Photo(documentId = documentId, file = frontName, type = "front"),
                Photo(documentId = documentId, file = backName, type = "back")).asList())

        savePhoto_(portraitName, portraitData)
        savePhoto_(frontName, frontData)
        savePhoto_(backName, backData)
        syncDataBase()
    }

    // Photo

    fun userDocumentPhotos(accountAddress: String, documentType: String) = photoDao.selectUserPhotosByDocument(accountAddress = accountAddress, documentType = documentType)

    fun userAddressPhoto(accountAddress: String) = photoDao.selectUserAddresPhoto(accountAddress = accountAddress)

    // Private

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

    // Backup

    private fun syncDataBase() {
        googleSignInAccount?.let {
            Handler().postDelayed({ SyncServise.getInstance().backupDatabase(Prefs.currentAccountAddress, "kimlic.db", appFolder = false, onSuccess = {}) }, 0)
        }
    }

    private fun syncPhoto(fileName: String) {
        googleSignInAccount?.let {
            val filePath = KimlicApp.applicationContext().filesDir.toString() + "/" + fileName
            Handler().postDelayed({
                SyncServise.getInstance().backupFile(accountAddress = Prefs.currentAccountAddress, filePath = filePath, appFolder = false, fileDescription = "photo", onSuccess = {})
            }, 0)
        }
    }

    // Sync user

    fun syncProfile(accountAddress: String) {
        val headers = mapOf(Pair("account-address", accountAddress))
        val syncRequest = KimlicRequest(Request.Method.GET, QuorumURL.profileSync.url, headers, null,
                Response.Listener {
                    val responceCode = JSONObject(it).getJSONObject("meta").optString("code").toString()

                    if (!responceCode.startsWith("2")) return@Listener

                    val jsonToParce = JSONObject(it).getJSONObject("data").getJSONArray("data_fields").toString()
                    val type = object : TypeToken<List<SyncObject>>() {}.type
                    val approvedObjects: List<SyncObject> = Gson().fromJson(jsonToParce, type)
                    val approved = approvedObjects.map { its -> its.name }

                    if (!approved.contains("phone")) contactDao.delete(Prefs.currentAccountAddress, "phone")
                    if (!approved.contains("email")) contactDao.delete(Prefs.currentAccountAddress, "email")
                    syncDataBase()
                },
                Response.ErrorListener {})

        Handler().post { VolleySingleton.getInstance(context = context).requestQueue.add(syncRequest) }
    }

    // RP request

    fun sendDoc(docType: String, onSuccess: () -> Unit, onError: () -> Unit) {
        // Log.d("TAGSENDOC", "in repository senDoc")
        // val image: String = imageBase64(this)

        val documents = documentDao.select(Prefs.currentAccountAddress)
        val document = documents.filter { it.type.equals(docType) }
        Log.d("TAGDOCUMENT", "doctype = " + docType)
        Log.d("TAGDOCUMENT", "chosen document - " + document)

        val documentFotos = photoDao.selectUserPhotosByDocument(Prefs.currentAccountAddress, docType)
        Log.d("TAGDOCUMENT", "photos = " + documentFotos.toString())
//      val image: String = File(context.filesDir.toString() + "/" + fileName).readText(charset = Charset.defaultCharset())

        val faceImage = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).readText()
        val faceImageIS = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).inputStream()
        val faceImageString = faceImageIS.bufferedReader().use { it.readText() }


        val frontImage = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).readText()
        val frontImageIS = File(context.filesDir.toString() + "/" + documentFotos.get(1).file).inputStream()
        val frontImageString = frontImageIS.bufferedReader().use { it.readText() }

        val backImage = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).readText()
        val backImageIS = File(context.filesDir.toString() + "/" + documentFotos.get(2).file).inputStream()
        val backImageString = backImageIS.bufferedReader().use { it.readText() }




        val shaFace = Sha.sha256(faceImageString)
        val shaFront = Sha.sha256(frontImageString)
        val shaBack = Sha.sha256(backImageString)


        //val url = "https://elixir.aws.pp.ua/api/medias"
        val url = "https://67a9c1a3.ngrok.io/api/medias"

        val receipt = QuorumKimlic.getInstance().setFieldMainData(
                "{\"face\":${shaFace},\"document-front\":${shaFront},\"document-back\":${shaBack}}",
                "documents.id_card")

        Log.e("RECEIPT", receipt.toString())

        Log.e("ACCOUNT", Prefs.currentAccountAddress)

        val paramsFace = JSONObject()
        paramsFace.put("attestator", "Veriff.me")
        paramsFace.put("doc", "ID_CARD")
        paramsFace.put("type", "face")
        paramsFace.put("file", faceImageString)
        Log.e("PARAMS", paramsFace.toString())

        ///////////////////////////////////////////////////////////////////
        val faceRequest = object : JsonObjectRequest(Request.Method.POST, url, paramsFace, Response.Listener<JSONObject> { response ->
            Log.e("DOC RESPONSE", response.toString())
        }, Response.ErrorListener { error ->


            //unableToProceed()
        }) {
            init {
                setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                        Pair("Account-Address", Prefs.currentAccountAddress),
                        Pair("Content-Type", "application/json; charset=utf-8"),
                        Pair("Accept", "application/vnd.mobile-api.v1+json")
                )
            }
        }
        /////////////////////////////////////////////////////////////////////

        val paramsFront = JSONObject()
        paramsFront.put("attestator", "Veriff.me")
        paramsFront.put("doc", "ID_CARD")
        paramsFront.put("type", "front")
        paramsFront.put("file", frontImageString)
        Log.e("PARAMS", paramsFront.toString())
        val frontRequest = object : JsonObjectRequest(Request.Method.POST, url, paramsFront, Response.Listener<JSONObject> { response ->
            Log.e("DOC RESPONSE", response.toString())
        }, Response.ErrorListener { error ->


            //unableToProceed()
        }) {
            init {
                setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                        Pair("Account-Address", Prefs.currentAccountAddress),
                        Pair("Content-Type", "application/json; charset=utf-8"),
                        Pair("Accept", "application/vnd.mobile-api.v1+json")
                )
            }
        }
        /////////////////////////////////////////////////////////////////////
        val paramsBack = JSONObject()
        paramsBack.put("attestator", "Veriff.me")
        paramsBack.put("doc", "ID_CARD")
        paramsBack.put("type", "back")
        paramsBack.put("file", backImageString)
        Log.e("PARAMS", paramsBack.toString())
        val backRequest = object : JsonObjectRequest(Request.Method.POST, url, paramsBack, Response.Listener<JSONObject> { response ->
            Log.e("DOC RESPONSE", response.toString())
        }, Response.ErrorListener { error ->

            //unableToProceed()
        }) {
            init {
                setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                        Pair("Account-Address", Prefs.currentAccountAddress),
                        Pair("Content-Type", "application/json; charset=utf-8"),
                        Pair("Accept", "application/vnd.mobile-api.v1+json")
                )
            }
        }
        //////////////////////////////////////////////////
        VolleySingleton.getInstance(context).addToRequestQueue(faceRequest)
        VolleySingleton.getInstance(context).addToRequestQueue(frontRequest)
        VolleySingleton.getInstance(context).addToRequestQueue(backRequest)

    }
}