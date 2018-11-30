package com.kimlic.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.DoAsync
import com.kimlic.API.KimlicApi
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.db.dao.CompanyDao
import com.kimlic.db.dao.CompanyDocumentDao
import com.kimlic.db.dao.DocumentDao
import com.kimlic.db.entity.Company
import com.kimlic.documents.Status
import com.kimlic.preferences.Prefs
import com.kimlic.service.entity.CompanyDocumentsEntity
import com.kimlic.service.entity.DocumentWrapperEntity
import com.kimlic.utils.AppConstants
import com.kimlic.utils.time_converter.TimeZoneConverter
import org.json.JSONObject
import java.util.*

class CompanyDetailsSyncService : IntentService(CompanyDetailsSyncService::class.java.simpleName) {

    // Variables

    private var dataBase: KimlicDB? = null
    private lateinit var companyDao: CompanyDao
    private lateinit var documentDao: DocumentDao
    private lateinit var companyDocumentDao: CompanyDocumentDao
    private lateinit var unverifiedList: List<Company>
    private lateinit var pendingList: List<Company>

    private lateinit var unverifiedQueue: ArrayDeque<Company>

    // Live

    override fun onCreate() {
        super.onCreate()

        dataBase = KimlicDB.getInstance()
        companyDao = dataBase!!.companyDao()
        documentDao = dataBase!!.documentDao()
        companyDocumentDao = dataBase!!.companyDocumentDao()
        unverifiedQueue = ArrayDeque()
    }

    override fun onHandleIntent(intent: Intent?) {
        unverifiedList = companyDao.companysByStatus(Prefs.currentAccountAddress, Status.UNVERIFIED.state)
        pendingList = companyDao.companysByStatus(Prefs.currentAccountAddress, Status.PENDING.state)
        unverifiedQueue.addAll(unverifiedList + pendingList)
        recursiveRequest()
    }

    // Private

    private fun recursiveRequest() {
        unverifiedQueue.poll()?.let { company ->
            val headers = mapOf(Pair("account-address", Prefs.currentAccountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"), Pair("Content-Type", "application/json"))

            val request = KimlicJSONRequest(GET, company.url + KimlicApi.DOCUMENTS_VERIFIED.path, headers, JSONObject(),
                    Response.Listener { JSONObject ->
                        val type = object : TypeToken<CompanyDocumentsEntity>() {}.type
                        val docList = Gson().fromJson<CompanyDocumentsEntity>(JSONObject.toString(), type)

                        docList.docs.forEach { updateCompanyDocumentDetails(Prefs.currentAccountAddress, company, it) }
                        recursiveRequest()
                    },
                    Response.ErrorListener {
                        Log.d("TAGSERVICE", "error sync - ${it.networkResponse}")
                    })

            VolleySingleton.getInstance(this).requestQueue.add(request)
        } ?: stopSelf()
    }

    // Private

    private fun updateCompanyDocumentDetails(accountAddress: String, company: Company, companyWrapperEntity: DocumentWrapperEntity) {
        val documentEntityType = companyWrapperEntity.document.type!!
        val document = documentDao.select(accountAddress, documentEntityType)
        val documentId = document?.id!!
        val companyId = company.id

        val companyDocumentJoin = companyDocumentDao.selectCompanyDocumentJoin(Prefs.currentAccountAddress, companyId)

        companyWrapperEntity.let {
            when (it.document.status) {
                // Pending status
                "" -> {
                    val verifiedAt = 0L
                    companyDocumentJoin.apply { date = verifiedAt }
                    company.status = Status.PENDING.state
                    companyDao.update(company)
                    companyDocumentDao.insert(companyDocumentJoin)
                }
                "declined" -> {
                    companyDocumentJoin.apply { date = -1L }
                    company.status = Status.UNVERIFIED.state
                    companyDao.update(company)
                    companyDocumentDao.insert(companyDocumentJoin)
                }
                "approved" -> {
                    val verifiedAt = if (it.document.verifiedAt != "") TimeZoneConverter().convertToSeconds(timeDate = it.document.verifiedAt!!) else 0
                    companyDocumentJoin.apply { date = verifiedAt }

                    updateUserName(it.document.firstName!!, it.document.lastName!!)
                    company.status = Status.VERIFIED.state
                    Prefs.needCompanySyncCount = Prefs.needCompanySyncCount - 1
                    companyDocumentDao.insert(companyDocumentJoin)
                    companyDao.update(company)

                    sendRedDotBroadcast()
                }
                "resubmission_requested" -> {
                }
                else -> {
                }
            }
        }

        syncDataBase()
    }

    private fun updateUserName(firstName: String, lastName: String) {
        val hasApprovedDocs = documentDao.select(Prefs.currentAccountAddress).filter { it.state == Status.VERIFIED.state }.size

        if (hasApprovedDocs > 0) return

        val user = dataBase!!.userDao().select(Prefs.currentAccountAddress)
        user.firstName = firstName
        user.lastName = lastName
        dataBase!!.userDao().update(user)
        syncDataBase()
    }

    private fun syncDataBase(onSuccess: () -> Unit = {}) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(application.applicationContext) // right here
        if (Prefs.isDriveActive && googleSignInAccount != null)
            DoAsync().execute(Runnable { SyncService.getInstance().backupDatabase(Prefs.currentAccountAddress, "kimlic.db", onSuccess = onSuccess, onError = {}) })
    }

    private fun sendRedDotBroadcast() {
        val intent = Intent(AppConstants.DETAILS_BROADCAST_ACTION.key)
        sendBroadcast(intent)
    }
}