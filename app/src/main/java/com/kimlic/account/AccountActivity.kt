package com.kimlic.account

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.model.ProfileViewModel

class AccountActivity : BaseActivity() {

    // Variables

    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)


        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        // show missing information
    }

    // Private

    private fun setupUI() {


    }
}