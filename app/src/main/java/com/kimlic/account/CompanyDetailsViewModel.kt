package com.kimlic.account

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.preferences.Prefs

class CompanyDetailsViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val companyRepository = CompanyRepository.instance

    // Public

    fun companiesLive() = companyRepository.companiesLive(Prefs.currentAccountAddress)

    fun company(companyId: String) = companyRepository.company(companyId)
}