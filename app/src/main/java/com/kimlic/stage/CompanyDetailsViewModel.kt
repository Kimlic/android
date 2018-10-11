package com.kimlic.stage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import com.kimlic.KimlicApp
import com.kimlic.account.CompanyRepository
import com.kimlic.db.entity.Company
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs
import com.kimlic.service.CompanyDetailsSyncService

class CompanyDetailsViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    // Variables

    private val companyRepository = CompanyRepository.instance
    private var profileRepository = ProfileRepository.instance

    // Public

    fun saveCompany(company: Company) {
        val userId = profileRepository.getUser(Prefs.currentAccountAddress).id
        company.userId = userId
        companyRepository.saveCompany(company)
    }

    fun companiesLive() = companyRepository.companiesLive(Prefs.currentAccountAddress)

    fun company(companyId: String) = companyRepository.company(companyId)

    fun companyLive(companyId: String) = companyRepository.companyLive(companyId)

    fun companyIds() = companyRepository.companyIds(Prefs.currentAccountAddress)

    fun companyDocumentDetails(companyId: String) = companyRepository.companyVerifiedDocument(Prefs.currentAccountAddress, companyId)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun companyStatusRequestService() {
        val syncIntent = Intent(getApplication(), CompanyDetailsSyncService::class.java)
        getApplication<KimlicApp>().startService(syncIntent)
    }
}