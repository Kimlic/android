package com.kimlic.account

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.preferences.Prefs

class AccountDetailsViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val accountRepository = AccountRepository.instance

    // Public

    fun accountsLive() = accountRepository.accountsLive(Prefs.currentAccountAddress)

    fun account(accountId: Long) = accountRepository.account(accountId)
}