package com.kimlic.account

import android.annotation.SuppressLint
import com.kimlic.db.KimlicDB
import com.kimlic.db.dao.CompanyDao

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


    init {
        companyDao = db!!.companyDao()
    }

    // Public

    fun accountsLive(accountAddress: String) = companyDao.companiesLive(accountAddress)

    fun company(id: String) = companyDao.company(id)

}