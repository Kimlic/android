package com.kimlic.account

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import kotlinx.android.synthetic.main.activity_account_details.*

class AccountDetails : BaseActivity() {

    // Variables

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        setupUI()
    }

    // Private

    private fun setupUI() {
        titleTv.text = "KimlickRp"
        subtitle1Tv.text = "subtitle Kimlick Rp"

    }


}