package com.kimlic.account

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.BuildConfig
import com.kimlic.R
import com.kimlic.db.entity.Account
import kotlinx.android.synthetic.main.activity_account_details.*

class AccountDetails : BaseActivity() {

    // Variables

    private lateinit var accountDetailModel: AccountDetailsViewModel
    private lateinit var rpAccount: Account

    private val uriKimlicExplorer = BuildConfig.KIMLIC_EXPLORER_URI

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        accountDetailModel = ViewModelProviders.of(this).get(AccountDetailsViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        val accountId = intent?.getStringExtra("accountId").orEmpty()

//        rpAccount = accountDetailModel.account(accountId = accountId.toLong())
//
//        with(rpAccount) {
//            titleTv.text = "KimlickRp"
//            subtitle1Tv.text = "subtitle Kimlick Rp"
//            Picasso.get...
//
//        }

        titleTv.text = "Kimlic.com"
        subtitle1Tv.text = "Identity verification by Veriff"
        subtitle2Tv.text = "Your folowing details to be shared"


        viewExplorerBt.setOnClickListener {
            val kimlicPage = Uri.parse(uriKimlicExplorer)
            val intent = Intent(Intent.ACTION_VIEW, kimlicPage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        unlinkBt.setOnClickListener {
            showToast("Unlick Account")
        }
    }
}