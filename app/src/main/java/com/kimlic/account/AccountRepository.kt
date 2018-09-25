package com.kimlic.account

import android.annotation.SuppressLint
import com.kimlic.BuildConfig
import com.kimlic.db.KimlicDB
import com.kimlic.db.dao.AccountDao

class AccountRepository {

    // Holder object

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = AccountRepository()
    }

    // Companion

    companion object {
        val instance: AccountRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Holder.INSTANCE }
        private const val API_URL = BuildConfig.API_CORE_URL
    }

    // Variables

    private val db = KimlicDB.getInstance()
    private var accountDao: AccountDao


    init {
        accountDao = db!!.accountDao()
    }

    // Public

    fun accountsLive(accountAddress: String) = accountDao.accountsLive(accountAddress)

    fun account(id: Long) = accountDao.account(id)

}