package com.kimlic.stage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.account.CompanyRepository
import com.kimlic.db.entity.Company
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs

class CompanyDetailsViewModel(application: Application) : AndroidViewModel(application) {

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

    fun companyIds() = companyRepository.companyIds(Prefs.currentAccountAddress)
}