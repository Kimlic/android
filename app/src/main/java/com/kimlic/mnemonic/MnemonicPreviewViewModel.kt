package com.kimlic.mnemonic

import android.arch.lifecycle.ViewModel
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs
import com.kimlic.utils.allopen.TestOpen

@TestOpen
class MnemonicPreviewViewModel : ViewModel() {

    // Variables

    private val repository = ProfileRepository.instance

    // Public

    fun userMnemonic() = repository.getUser(Prefs.currentAccountAddress).mnemonic
}