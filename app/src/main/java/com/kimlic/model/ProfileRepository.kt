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
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.KimlicRequest
import com.kimlic.API.SyncObject
import com.kimlic.API.VolleySingleton
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.QuorumURL
import com.kimlic.utils.mappers.BitmapToByteArrayMapper
import org.json.JSONObject
import org.spongycastle.util.encoders.Base64
import java.io.*
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
    private var vendorDao: VendorDao
    private var context: Context

    // Init

    init {
        db = KimlicDB.getInstance()!!
        userDao = db.userDao()
        contactDao = db.contactDao()
        documentDao = db.documentDao()
        addressDao = db.addressDao()
        photoDao = db.photoDao()
        vendorDao = db.vendorDao()
        //googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())
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

    fun documents(accountAddress: String) = documentDao.select(accountAddress = accountAddress)

    fun documentsLive(accountAddress: String) = documentDao.selectLive(accountAddress = accountAddress)

    fun document(accountAddress: String, documentType: String) = documentDao.select(accountAddress = accountAddress, documentType = documentType)

    fun documentDelete(documentId: Long) = { documentDao.delete(id = documentId); syncDataBase() }

    fun addDocument(accountAddress: String, documentType: String, portraitName: String, portraitData: ByteArray, frontName: String, frontData: ByteArray, backName: String, backData: ByteArray) {
        val userId = userDao.select(accountAddress = accountAddress).id
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

    fun updateDocument(document: Document) {
        documentDao.update(document); syncDataBase()
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
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        Log.d("TAGSYNC", "syncDatabase " + googleSignInAccount)
        googleSignInAccount?.let {
            Handler().postDelayed({ SyncService.getInstance().backupDatabase(Prefs.currentAccountAddress, "kimlic.db", onSuccess = {}) }, 0)
        }
    }

    fun clearAllFiles() {
        val rootFilesDir = File(context.filesDir.toString())
        val files = rootFilesDir.listFiles()
        files.forEach { it.delete() }
    }

    private fun syncPhoto(fileName: String) {
        googleSignInAccount?.let {
            val filePath = KimlicApp.applicationContext().filesDir.toString() + "/" + fileName
            Handler().postDelayed({
                SyncService.getInstance().backupFile(accountAddress = Prefs.currentAccountAddress, filePath = filePath, fileDescription = SyncService.PHOTO_DESCRIPTION, onSuccess = {})
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

//    fun sendDoc_(docType: String, onSuccess: () -> Unit, onError: () -> Unit) {
//        // Log.d("TAGSENDOC", "in repository senDoc")
//        // val image: String = imageBase64(this)
//
//        val documents = documentDao.select(Prefs.currentAccountAddress)
//        val document = documents.filter { it.type.equals(docType) }
//        Log.d("TAGDOCUMENT", "doctype = " + docType)
//        Log.d("TAGDOCUMENT", "chosen document - " + document)
//
//        val documentFotos = photoDao.selectUserPhotosByDocument(Prefs.currentAccountAddress, docType)
//
//
//
//        Log.d("TAGDOCUMENT", "photos = " + documentFotos.toString())
////      val image: String = File(context.filesDir.toString() + "/" + fileName).readText(charset = Charset.defaultCharset())
//
//        val faceImage = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).readText()
//        val faceImageIS = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).inputStream()
//        val faceImageString = faceImageIS.bufferedReader().use { it.readText() }
//
//
//        val frontImage = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).readText()
//        val frontImageIS = File(context.filesDir.toString() + "/" + documentFotos.get(1).file).inputStream()
//        val frontImageString = frontImageIS.bufferedReader().use { it.readText() }
//
//        val backImage = File(context.filesDir.toString() + "/" + documentFotos.get(0).file).readText()
//        val backImageIS = File(context.filesDir.toString() + "/" + documentFotos.get(2).file).inputStream()
//        val backImageString = backImageIS.bufferedReader().use { it.readText() }
//
//
//        val shaFace = Sha.sha256(faceImageString)
//        val shaFront = Sha.sha256(frontImageString)
//        val shaBack = Sha.sha256(backImageString)
//
//
//        //val url = "https://elixir.aws.pp.ua/api/medias"
//        val url = "https://098923f7.ngrok.io/api/medias"
//
//        val receipt = QuorumKimlic.getInstance().setFieldMainData(
//                "{\"face\":${shaFace},\"document-front\":${shaFront},\"document-back\":${shaBack}}",
//                "documents.id_card")
//
//        Log.e("RECEIPT", receipt.toString())
//
//        Log.e("ACCOUNT", Prefs.currentAccountAddress)
//
//        val paramsFace = JSONObject()
//        paramsFace.put("attestator", "Veriff.me")
//        paramsFace.put("doc", "ID_CARD")
//        paramsFace.put("type", "face")
//        paramsFace.put("file", faceImageString)
//        Log.e("PARAMS", paramsFace.toString())
//
//        ///////////////////////////////////////////////////////////////////
//        val faceRequest = object : JsonObjectRequest(Request.Method.POST, url, paramsFace, Response.Listener<JSONObject> { response ->
//            Log.e("DOC RESPONSE", response.toString())
//        }, Response.ErrorListener { error ->
//
//
//            //unableToProceed()
//        }) {
//            init {
//                setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                return mapOf(
//                        Pair("Account-Address", Prefs.currentAccountAddress),
//                        Pair("Content-Type", "application/json; charset=utf-8"),
//                        Pair("Accept", "application/vnd.mobile-api.v1+json")
//                )
//            }
//        }
//        /////////////////////////////////////////////////////////////////////
//
//        val paramsFront = JSONObject()
//        paramsFront.put("attestator", "Veriff.me")
//        paramsFront.put("doc", "ID_CARD")
//        paramsFront.put("type", "front")
//        paramsFront.put("file", frontImageString)
//        Log.e("PARAMS", paramsFront.toString())
//        val frontRequest = object : JsonObjectRequest(Request.Method.POST, url, paramsFront, Response.Listener<JSONObject> { response ->
//            Log.e("DOC RESPONSE", response.toString())
//        }, Response.ErrorListener { error ->
//
//
//            //unableToProceed()
//        }) {
//            init {
//                setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                return mapOf(
//                        Pair("Account-Address", Prefs.currentAccountAddress),
//                        Pair("Content-Type", "application/json; charset=utf-8"),
//                        Pair("Accept", "application/vnd.mobile-api.v1+json")
//                )
//            }
//        }
//        /////////////////////////////////////////////////////////////////////
//        val paramsBack = JSONObject()
//        paramsBack.put("attestator", "Veriff.me")
//        paramsBack.put("doc", "ID_CARD")
//        paramsBack.put("type", "back")
//        paramsBack.put("file", backImageString)
//        Log.e("PARAMS", paramsBack.toString())
//        val backRequest = object : JsonObjectRequest(Request.Method.POST, url, paramsBack, Response.Listener<JSONObject> { response ->
//            Log.e("DOC RESPONSE", response.toString())
//        }, Response.ErrorListener { error ->
//
//            //unableToProceed()
//        }) {
//            init {
//                setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                return mapOf(
//                        Pair("Account-Address", Prefs.currentAccountAddress),
//                        Pair("Content-Type", "application/json; charset=utf-8"),
//                        Pair("Accept", "application/vnd.mobile-api.v1+json")
//                )
//            }
//        }
//        //////////////////////////////////////////////////
//        VolleySingleton.getInstance(context).addToRequestQueue(faceRequest)
////        VolleySingleton.getInstance(context).addToRequestQueue(frontRequest)
////        VolleySingleton.getInstance(context).addToRequestQueue(backRequest)
//
//    }

    //val url = "https://elixir.aws.pp.ua/api/medias"
    // val url = "https://67a9c1a3.ngrok.io/api/medias"

//    fun mySendoc_(documentType: String, dynamicUrl: String = "https://67a9c1a3.ngrok.io/api/medias", onSuccess: () -> Unit, onError: () -> Unit) {
//        val requestList = mutableListOf<KimlicRequest>()
//        val datas = mutableListOf<String>()
//        val dataType: String
//        val docType: String
//        val dataValue: String
//
//        //@formatter:off
//        when (documentType) {
//            "passport" -> { docType = "PASSPORT"; dataType = "documents.passport" }
//            "id" -> { docType = "ID_CARD"; dataType = "documents.id_card" }
//            "license" -> { docType = "DRIVERS_LICENSE"; dataType = "documents.drivers_license" }
//            "permit" -> { docType = "RESIDENCE_PERMIT_CARD"; dataType = "documents.residence_permit_card"}
//            else -> { docType = ""; dataType = "" }
//        }
//        //@formatter:on
//
//        val document = documentDao.select(accountAddress = Prefs.currentAccountAddress, documentType = documentType)
//        val documentPhotoList = photoDao.selectUserPhotosByDocument(accountAddress = Prefs.currentAccountAddress, documentType = documentType)
//        val vendorDocument = vendorDao.select().find { it.type.equals(docType) }
//
//        if (vendorDocument!!.contexts.contains("face")) {
//            val faceImage = File(context.filesDir.toString() + "/" + documentPhotoList.get(0).file).readText()
//            val faceParams = mapOf("attestator" to "Veriff.me", "doc" to docType, "type" to "face", "file" to faceImage)
//            val shaFace = Sha.sha256(faceImage)
//            datas.add("\"face\":${shaFace}")
//            val faceRequest = KimlicRequest(POST, dynamicUrl, headers(), faceParams, Response.Listener { }, Response.ErrorListener { onError })
//            requestList.add(faceRequest)
//        }
//
//        if (vendorDocument.contexts.contains("document-front")) {
//            val frontImage = File(context.filesDir.toString() + "/" + documentPhotoList.get(1).file).readText()
//            val frontParams = mapOf("attestator" to "Veriff.me", "doc" to docType, "type" to "document-front", "file" to frontImage)
//            val shaFront = Sha.sha256(frontImage)
//            datas.add("\"document-front\":${shaFront}")
//            val frontRequest = KimlicRequest(POST, dynamicUrl, headers(), frontParams, Response.Listener { }, Response.ErrorListener { onError })
//            requestList.add(frontRequest)
//        }
//
//        if (vendorDocument.contexts.contains("document-back")) {
//            val backImage = File(context.filesDir.toString() + "/" + documentPhotoList.get(2).file).readText()
//            val backParams = mapOf("attestator" to "Veriff.me", "doc" to docType, "type" to "document-back", "file" to backImage)
//            val shaBack = Sha.sha256(backImage)
//            datas.add("\"document-back\":${shaBack}")
//            val backRequest = KimlicRequest(POST, dynamicUrl, headers(), backParams, Response.Listener { }, Response.ErrorListener { onError })
//            requestList.add(backRequest)
//        }
//
//        dataValue = "{" + datas.joinToString(",") + "}"
//
//        val receipt = QuorumKimlic.getInstance().setFieldMainData(dataValue, dataType)
//
//
//        Log.d("TAGSENDOC", "receipt before")
//        receipt?.let {
//            Log.d("TAGSENDOC", "receipt  ${it}")
//            requestList.forEach { request ->
//                VolleySingleton.getInstance(context).addToRequestQueue(request)
//            }
//        }
//    }

    // Private helpers

//    private fun headers(): HashMap<String, String> {
//        val headers = HashMap<String, String>()
//        headers["Account-Address"] = Prefs.currentAccountAddress
//        headers["Content-Type"] = "application/json; charset=utf-8"
//        headers["Accept"] = "application/vnd.mobile-api.v1+json"
//        return headers
//    }

//    fun imageBase64Face(context: Context): String {
//        val stream = context.resources.openRawResource(R.raw.base64face)
//
//        return BufferedReader(InputStreamReader(stream, "UTF-8")).readLine()!!
//    }
//
//    fun imageBase64Front(context: Context): String {
//        val stream = context.resources.openRawResource(R.raw.base64front)
//
//        return BufferedReader(InputStreamReader(stream, "UTF-8")).readLine()!!
//    }
//
//    fun imageBase64Back(context: Context): String {
//        val stream = context.resources.openRawResource(R.raw.base64back)
//
//        return BufferedReader(InputStreamReader(stream, "UTF-8")).readLine()!!
//    }

    fun sendDoc(documentType: String, dynamicUrl: String = "https://3ac29596.ngrok.io/api/medias", country: String, onSuccess: () -> Unit, onError: () -> Unit) {
//    fun sendDoc(documentType: String, dynamicUrl: String = "https://elixir.aws.pp.ua/api/medias", country: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val documents = documentDao.select(Prefs.currentAccountAddress)
        val document = documents.filter { it.type.equals(documentType) }
        val user = userDao.select(Prefs.currentAccountAddress)
        val doc: String
        val dataType: String
        val firstName = user.firstName
        val lastName = user.lastName
        val udid = FirebaseInstanceId.getInstance().token!!
        Log.d("TAGUDID", "udid = ${udid}")
        Log.d("TAGUDID", "firstName = $firstName")
        Log.d("TAGUDID", "lastNAme = $lastName")
        Log.d("TAGUDID", "country = $country")
        Log.d("TAGUDID", "url = $dynamicUrl")


        //@formatter:off
        when (documentType) {
            "passport" -> { doc = "PASSPORT"; dataType = "documents.passport" }
            "id" -> { doc = "ID_CARD"; dataType = "documents.id_card" }
            "license" -> { doc = "DRIVERS_LICENSE"; dataType = "documents.drivers_license" }
            "permit" -> { doc = "RESIDENCE_PERMIT_CARD"; dataType = "documents.residence_permit_card"}
            else -> { doc = ""; dataType = "" }
        }
        //@formatter:on

        Log.d("TAGDOCUMENT", "doctype = " + documentType)
        Log.d("TAGDOCUMENT", "chosen document - " + document)

        val documentPhotos = photoDao.selectUserPhotosByDocument(Prefs.currentAccountAddress, documentType)

        val faceString = File(context.filesDir.toString() + "/" + documentPhotos[0].file).readText(charset = Charset.forName("UTF-8"))
        val frontString = File(context.filesDir.toString() + "/" + documentPhotos[1].file).readText(charset = Charset.forName("UTF-8"))
        val backString = File(context.filesDir.toString() + "/" + documentPhotos[2].file).readText(charset = Charset.forName("UTF-8"))

        //val faceImageString =

        val shaFace = Sha.sha256(faceString)
        val shaFront = Sha.sha256(frontString)
        val shaBack = Sha.sha256(backString)

        val receipt = QuorumKimlic.getInstance().setFieldMainData("{\"face\":${shaFace},\"document-front\":${shaFront},\"document-back\":${shaBack}}", dataType)//)"documents.id_card")
        Log.e("RECEIPT", receipt.toString())

        Handler().post {
            send(fileString = faceString, type = "face", doc = doc, firstName = firstName, lastName = lastName, country = country, udid = udid, url = dynamicUrl, listener = Response.Listener { response ->
                //Log.e("FACE", "SENT: " + response.toString())
                send(fileString = frontString, type = "document-front", doc = doc, firstName = firstName, lastName = lastName, country = country, udid = udid, url = dynamicUrl, listener = Response.Listener { response ->
                    //Log.e("FRONT", "SENT: " + response.toString())
                    send(fileString = backString, type = "document-back", doc = doc, firstName = dynamicUrl, lastName = lastName, country = country, udid = udid, url = dynamicUrl, listener = Response.Listener { response ->
                        //Log.e("BACK", "SENT: " + response.toString())
                        onSuccess()
                    }, onError = onError)
                }, onError = onError)
            }, onError = onError)
        }


    }

    private fun send(fileString: String, type: String, doc: String, firstName: String, lastName: String, country: String, udid: String, url: String, listener: Response.Listener<JSONObject>, onError: () -> Unit) {
//    private fun send(fileString: String, type: String, doc: String, firstName: String, lastName: String, country: String, udid: String? = "", url: String, listener: Response.Listener<JSONObject>, onError: () -> Unit) {

        val params = JSONObject()
        params.put("attestator", "Veriff.me")
        params.put("doc", doc)//"ID_CARD")
        params.put("type", type)
        params.put("udid", udid)//"\"dfPPl3RrZEk:APA91bGXIfSG0J_sX1Ts0e_3-WG1m6zpiirDkhJS7yo6gvWaF7yrteaTBdVt0cb8T9hxc1GbUVGdn7q6s3wwi8CtN2441Vi28mB1d4ptT0pwoMy-oz0Wo3jYqDO47aUA6YHu4vNNhSTQl-Cjn4M6eid_9Au6INMNXw\"")
        params.put("first_name", "John")
        params.put("last_name", "Doe")
        params.put("device", "android")
        params.put("country", country.toUpperCase())
        Log.d("PARAMS", params.toString())
        params.put("file", fileString)


        val request = object : JsonObjectRequest(Request.Method.POST, url, params, listener, Response.ErrorListener { error ->
            onError()
            Log.e("DOC RESPONSE ERROR", error.toString())
        }) {
            init {
                retryPolicy = DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
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
        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }
}