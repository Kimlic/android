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
import com.android.volley.Request.Method.GET
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.DoAsync
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.SyncObject
import com.kimlic.API.VolleySingleton
import com.kimlic.BuildConfig
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.API.KimlicApi
import com.kimlic.utils.mappers.BitmapToByteArrayMapper
import org.json.JSONObject
import org.spongycastle.util.encoders.Base64
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ExecutionException

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

    private val BASE_URL = BuildConfig.BASE_URL
    // Init

    init {
        db = KimlicDB.getInstance()!!
        userDao = db.userDao()
        contactDao = db.contactDao()
        documentDao = db.documentDao()
        addressDao = db.addressDao()
        photoDao = db.photoDao()
        vendorDao = db.vendorDao()
        context = KimlicApp.applicationContext()
    }

    // Public

    // User

    fun updateUser(user: User) = userDao.update(user)

    fun getUser(accountAddress: String) = userDao.select(accountAddress)

    fun deleteUser(accountAddress: String) {
        userDao.delete(accountAddress); syncDataBase()
    }

    fun userLive(accountAddress: String): LiveData<User> = userDao.selectLive(accountAddress)

    fun addUserPhoto(accountAddress: String, fileName: String, data: ByteArray) {
        savePhoto(fileName = fileName, data = data)
        savePhoto(fileName = "preview_" + fileName, data = croppedPreviewInByteArray(data))
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
        val addressPhoto = Photo(addressId = addressId, type = AppConstants.photoAddressType.key, file = fileName)
        photoDao.insert(photos = arrayOf(addressPhoto).asList())
        syncDataBase()
        savePhoto(fileName, data)
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
                Photo(documentId = documentId, file = portraitName, type = AppConstants.photoFaceType.key),
                Photo(documentId = documentId, file = frontName, type = AppConstants.photoFrontType.key),
                Photo(documentId = documentId, file = backName, type = AppConstants.photoBackType.key)).asList())

        savePhoto(portraitName, portraitData)
        savePhoto(frontName, frontData)
        savePhoto(backName, backData)
        syncDataBase()
    }

    fun updateDocument(document: Document) {
        documentDao.update(document); syncDataBase()
    }

    // Photo

    fun userDocumentPhotos(accountAddress: String, documentType: String) = photoDao.selectUserPhotosByDocument(accountAddress = accountAddress, documentType = documentType)

    fun userAddressPhoto(accountAddress: String) = photoDao.selectUserAddresPhoto(accountAddress = accountAddress)

    // Private

    private fun croppedPreviewInByteArray(data: ByteArray): ByteArray {
        val originalBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val resizedBitmap = getResizedBitmap(originalBitmap, 1024, 768, -90f, true)
        val width = resizedBitmap.width
        val height = resizedBitmap.height
        val croppedBitmap = Bitmap.createBitmap(resizedBitmap, (0.1 * width).toInt(), (0.12 * height).toInt(), (0.70 * width).toInt(), (0.72 * height).toInt())
        val croppedPreviewByteArray = BitmapToByteArrayMapper().transform(croppedBitmap)
        return croppedPreviewByteArray
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
                SyncService.getInstance().backupFile(Prefs.currentAccountAddress, filePath, SyncService.PHOTO_DESCRIPTION, onSuccess = {})
            }, 0)
        }
    }

    // New User

    fun initNewUserRegistration(onSuccess: () -> Unit, onError: () -> Unit) {
        val url = BASE_URL + KimlicApi.CONFIG.path
        // 1. Create Quorum instance locally - mnemonic and address
        QuorumKimlic.destroyInstance()
        QuorumKimlic.createInstance(null, context) // moved to QUORUM request

        val mnemonic = QuorumKimlic.getInstance().mnemonic
        val walletAddress = QuorumKimlic.getInstance().walletAddress
        // Init new user
        val user = User(accountAddress = walletAddress, mnemonic = mnemonic)
        // 2. Get entry point of the Quorum
        val headers = mapOf<String, String>(Pair("account-address", walletAddress))

        val addressRequest = KimlicJSONRequest(GET, url, headers, JSONObject(),
                Response.Listener {
                    if (!it.getJSONObject("meta").optString("code").startsWith("2")) {
                        onError(); return@Listener
                    }

                    val contextContractAddress = it.getJSONObject("data").optString("context_contract")
                    QuorumKimlic.getInstance().setKimlicContractsContextAddress(contextContractAddress)
                    val accountStorageAdapterAddress = QuorumKimlic.getInstance().accountStorageAdapter
                    QuorumKimlic.getInstance().setAccountStorageAdapterAddress(accountStorageAdapterAddress)
                    userDao.insert(user); syncDataBase();
                    Prefs.currentAccountAddress = walletAddress

                    onSuccess()
                },
                Response.ErrorListener { onError() })
        VolleySingleton.getInstance(context).addToRequestQueue(addressRequest)
    }

    // Requests

    // Quorum request

    fun quorumRequest(accountAddress: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val url = BASE_URL + KimlicApi.CONFIG.path
        val user = userDao.select(accountAddress)
        // 1. Create Quorum instance with current user

        val mnemonic = user.mnemonic
        QuorumKimlic.destroyInstance()
        QuorumKimlic.createInstance(mnemonic, context)//Create Quorum instance

        val walletAddress = user.accountAddress

        // 2. Get entry point of the Quorum
        val headers = mapOf(Pair("account-address", walletAddress))

        val addressRequest = KimlicJSONRequest(GET, url, headers, JSONObject(), Response.Listener {
            if (!it.getJSONObject("meta").optString("code").startsWith("2")) {
                onError(); return@Listener
            }

            val contextContractAddress = it.getJSONObject("data").optString("context_contract")
            QuorumKimlic.getInstance().setKimlicContractsContextAddress(contextContractAddress)

            val accountStorageAdapterAddress = QuorumKimlic.getInstance().accountStorageAdapter
            QuorumKimlic.getInstance().setAccountStorageAdapterAddress(accountStorageAdapterAddress)
            onSuccess()

        }, Response.ErrorListener { onError() })

        VolleySingleton.getInstance(context).addToRequestQueue(addressRequest)
    }

    // Sync user

    fun syncProfile(accountAddress: String) {
        val url = BASE_URL + KimlicApi.PROFILE_SYNC.path
        val headers = mapOf(Pair("account-address", accountAddress))

        val syncRequest = KimlicJSONRequest(GET, url, headers, JSONObject(), Response.Listener {
            if (!it.getJSONObject("meta").optString("code").startsWith("2")) return@Listener

            val jsonToParse = it.getJSONObject("data").getJSONArray("data_fields").toString()
            val type = object : TypeToken<List<SyncObject>>() {}.type
            val approvedObjects: List<SyncObject> = Gson().fromJson(jsonToParse, type)
            val approved = approvedObjects.map { its -> its.name }

            if (!approved.contains("phone")) contactDao.delete(Prefs.currentAccountAddress, "phone")
            if (!approved.contains("email")) contactDao.delete(Prefs.currentAccountAddress, "email")
            Log.d("TAGSYNC", "json to parse = $jsonToParse")
            syncDataBase()

        }, Response.ErrorListener { })

        Handler().post { VolleySingleton.getInstance(context = context).requestQueue.add(syncRequest) }
    }

    // Contacts verification

    fun contactVerify(target: String, source: String, onSuccess: () -> Unit, onError: () -> Unit) {
        DoAsync().execute(Runnable {
            val quorumKimlic = QuorumKimlic.getInstance()
            var receiptPhone: TransactionReceipt? = null

            try {
                receiptPhone = quorumKimlic.setFieldMainData(Sha.sha256(source), target)
            } catch (e: ExecutionException) {
                onError()
            } catch (e: InterruptedException) {
                onError()
            }

            if (receiptPhone != null && receiptPhone.transactionHash.isNotEmpty()) {
                val headers = mapOf(Pair("account-address", Prefs.currentAccountAddress))
                val params = JSONObject().put(target, source)

                val url =
                        when (target) {
                            "phone" -> BASE_URL + KimlicApi.PHONE_VERIFY.path
                            "email" -> BASE_URL + KimlicApi.EMAIL_VERIFY.path
                            else -> " "
                        }

                val verifyRequest = KimlicJSONRequest(POST, url, headers, params, Response.Listener {
                    if (!it.getJSONObject("meta").optString("code").startsWith("2")) {
                        onError(); return@Listener
                    }
                    onSuccess()

                }, Response.ErrorListener { onError() })

                VolleySingleton.getInstance(context).addToRequestQueue(verifyRequest)
            }
        })
    }

    fun contactApprove(target: String, code: String, onSuccess: () -> Unit, onError: (code: String) -> Unit) {
        DoAsync().execute(Runnable {
            val url = when (target) {
                "email" -> BASE_URL + KimlicApi.EMAIL_APPROVE.path
                "phone" -> BASE_URL + KimlicApi.PHONE_APPROVE.path
                else -> ""
            }

            val headers = mapOf(Pair("account-address", Prefs.currentAccountAddress))
            val params = JSONObject().put("code", code)

            val approveRequest = KimlicJSONRequest(POST, url, headers, params, Response.Listener {
                val responseSuccess = it.getJSONObject("meta").optString("code").startsWith("2")
                val statusOk = it.getJSONObject("data").optString("status").toString() == "ok"

                if (!responseSuccess && !statusOk) {
                    onError("400"); return@Listener
                }
                onSuccess()

            }, Response.ErrorListener {
                onError(it?.networkResponse?.statusCode.toString())
            })

            VolleySingleton.getInstance(context).addToRequestQueue(approveRequest)
        })
    }

    // RP request

    fun sendDoc(documentType: String, url: String = "http://40.113.76.56:4000/api/medias", countrySH: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val user = userDao.select(Prefs.currentAccountAddress)
        val doc: String
        val dataType: String
        val firstName = user.firstName
        val lastName = user.lastName
        val udid = FirebaseInstanceId.getInstance().token!!

        //@formatter:off
        when (documentType) {
            AppDoc.PASSPORT.type -> { doc = "PASSPORT"; dataType = "documents.passport" }
            AppDoc.ID_CARD.type -> { doc = "ID_CARD"; dataType = "documents.id_card" }
            AppDoc.DRIVERS_LICENSE.type -> { doc = "DRIVERS_LICENSE"; dataType = "documents.driver_license" }
            AppDoc.RESIDENCE_PERMIT_CARD.type -> { doc = "RESIDENCE_PERMIT_CARD"; dataType = "documents.residence_permit_card"}
            else -> { doc = ""; dataType = "" }
        }
        //@formatter:on
        val documentPhotos = photoDao.selectUserPhotosByDocument(Prefs.currentAccountAddress, documentType)

        val faceString = photoString(documentPhotos.first { it.type == AppConstants.photoFaceType.key }.file)
        val frontString = photoString(documentPhotos.first { it.type == AppConstants.photoFrontType.key }.file)
        val backString = photoString(documentPhotos.first { it.type == AppConstants.photoBackType.key }.file)

        val shaFace = Sha.sha256(faceString)
        val shaFront = Sha.sha256(frontString)
        val shaBack = Sha.sha256(backString)

        DoAsync().execute(Runnable {
            val receipt = QuorumKimlic.getInstance().setFieldMainData("{\"face\":${shaFace},\"document-front\":${shaFront},\"document-back\":${shaBack}}", dataType)

            if (receipt == null || receipt.status == "0x0") {
                onError()
            }
            send(faceString, "face", doc, firstName, lastName, countrySH, udid, url, Response.Listener { _ ->
                send(frontString, "document-front", doc, firstName, lastName, countrySH, udid, url, Response.Listener { _ ->
                    send(backString, "document-back", doc, url, lastName, countrySH, udid, url, Response.Listener { _ ->
                        onSuccess()
                    }, onError)
                }, onError)
            }, onError)
        })
    }

    private fun send(fileString: String, type: String, doc: String, firstName: String, lastName: String, countrySH: String, udid: String, url: String, listener: Response.Listener<JSONObject>, onError: () -> Unit) {
        val params = JSONObject()
        params.put("attestator", "Veriff.me")
        params.put("doc", doc)
        params.put("type", type)
        params.put("udid", udid)
        params.put("first_name", firstName)
        params.put("last_name", lastName)
        params.put("device", "android")
        params.put("country", countrySH.toUpperCase())
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

    fun senDoc_(documentType: String, url: String = "http://40.113.76.56:4000/api/medias", countrySH: String, onSuccess: () -> Unit, onError: () -> Unit) {
        var faceRequest: JsonObjectRequest? = null
        val queue = ArrayDeque<JsonObjectRequest>()
        val shas = mutableListOf<String>()

        val dataType: String
        val docType: String
        val dataValue: String

        val user = userDao.select(accountAddress = Prefs.currentAccountAddress)
        val firstName = user.firstName
        val lastName = user.lastName
        val udid = FirebaseInstanceId.getInstance().token!!

        //@formatter:off
        when (documentType) {
            "passport" -> { docType = "PASSPORT"; dataType = "documents.passport" }
            "id" -> { docType = "ID_CARD"; dataType = "documents.id_card" }
            "license" -> { docType = "DRIVERS_LICENSE"; dataType = "documents.driver_license" }
            "permit" -> { docType = "RESIDENCE_PERMIT_CARD"; dataType = "documents.residence_permit_card"}
            else -> { docType = ""; dataType = "" }
        }
        //@formatter:on

        val params = params(docType, udid, firstName, lastName, countrySH)
        val documents = photoDao.selectUserPhotosByDocument(Prefs.currentAccountAddress, documentType)
        val vendorDocument = vendorDao.select().find { it.type == docType }

        if (vendorDocument!!.contexts.contains("face")) {
            val faceImage = photoString(documents.first { it.type == AppConstants.photoFaceType.key }.file)
            val faceParams = params.put("type", "face").put("file", faceImage)
            val shaFace = Sha.sha256(faceImage)
            shas.add("\"face\":${shaFace}")

            faceRequest = docRequest(POST, url, faceParams, Response.Listener { nextRequest(queue, onSuccess) }, Response.ErrorListener { onError() })
        }

        if (vendorDocument.contexts.contains("document-front")) {
            val frontImage = photoString(documents.first { it.type == AppConstants.photoFrontType.key }.file)
            val frontParams = params.put("type", "document-front").put("file", frontImage)
            val shaFront = Sha.sha256(frontImage)
            val frontRequest = docRequest(POST, url, frontParams, Response.Listener { nextRequest(queue, onSuccess) }, Response.ErrorListener { onError() })

            queue.add(frontRequest)
            shas.add("\"document-front\":${shaFront}")
        }

        if (vendorDocument.contexts.contains("document-back")) {
            val backImage = photoString(documents.first { it.type == AppConstants.photoBackType.key }.file)
            val shaBack = Sha.sha256(backImage)
            val backParams = params.put("type", "document-back").put("file", backImage)
            val backRequest = docRequest(POST, url, backParams, Response.Listener { nextRequest(queue, onSuccess) }, Response.ErrorListener { onError() })

            shas.add("\"document-back\":${shaBack}")
            queue.add(backRequest)
        }

        dataValue = "{" + shas.joinToString(",") + "}"
        val receipt = QuorumKimlic.getInstance().setFieldMainData(dataValue, dataType)

        if (receipt.status == "0x0") {
            Log.e("RECEIPT ERROR", receipt.toString())
            onError()
            return
        }

        VolleySingleton.getInstance(context).addToRequestQueue(faceRequest!!)
    }

    private fun nextRequest(queue: Queue<JsonObjectRequest>, onSuccess: () -> Unit) {
        if (queue.peek() != null) {
            VolleySingleton.getInstance(context).addToRequestQueue(queue.poll())
        } else onSuccess()
    }

    private fun params(docType: String, udid: String, firstName: String, lastName: String, countrySH: String): JSONObject {
        val params = JSONObject()
        params.put("attestator", "Veriff.me")
        params.put("doc", docType)
        params.put("udid", udid)
        params.put("first_name", firstName)
        params.put("last_name", lastName)
        params.put("device", "android")
        params.put("country", countrySH.toUpperCase())
        return params
    }

    private fun docRequest(method: Int, url: String, params: JSONObject, listener: Response.Listener<JSONObject>, error: Response.ErrorListener): JsonObjectRequest {
        val request = object : JsonObjectRequest(method, url, params, listener, error) {init {
            retryPolicy = DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                        Pair("Account-Address", Prefs.currentAccountAddress),
                        Pair("Content-Type", "application/json; charset=utf-8"),
                        Pair("Accept", "application/vnd.mobile-api.v1+json"))
            }
        }
        return request
    }

    // Private helpers

    private fun photoString(fileName: String) = File(context.filesDir.toString() + "/" + fileName).readText(Charset.forName("UTF-8"))

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

        if (!isNecessaryToKeepOrig) bm.recycle()

        return resizedBitmap
    }

    private fun savePhoto(fileName: String, data: ByteArray) {
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
}