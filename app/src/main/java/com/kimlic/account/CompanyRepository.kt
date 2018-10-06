package com.kimlic.account

import android.annotation.SuppressLint
import com.kimlic.db.KimlicDB
import com.kimlic.db.dao.CompanyDao
import com.kimlic.db.dao.CompanyDocumentDao
import com.kimlic.db.entity.Company

class CompanyRepository {

    // Holder object

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = CompanyRepository()
    }

    // Companion

    companion object {
        val instance: CompanyRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Holder.INSTANCE }
    }

    // Variables

    private val db = KimlicDB.getInstance()
    private var companyDao: CompanyDao
    private var companyDocumentDao: CompanyDocumentDao


    init {
        companyDao = db!!.companyDao()
        companyDocumentDao = db.companyDocumentDao()
    }

    // Public

    fun saveCompany(company: Company) = companyDao.insert(company)

    fun companiesLive(accountAddress: String) = companyDao.companiesLive(accountAddress)

    fun companyIds(accountAddress: String) = companyDao.companyIds(accountAddress)

    fun company(id: String) = companyDao.company(id)

    fun companyLive(id: String) = companyDao.companyLive(id)

    // Company verified document

    fun companyVerifiedDocument(accountAddress: String, companyId: String) = companyDocumentDao.selectCompanyDocumentJoin(accountAddress, companyId)

}