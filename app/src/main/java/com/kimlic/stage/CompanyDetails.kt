package com.kimlic.stage

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.kimlic.BaseActivity
import com.kimlic.BuildConfig
import com.kimlic.R
import com.kimlic.account.CompanyDetailsViewModel
import com.kimlic.db.entity.Company
import com.kimlic.stage.adapter.CompanyDetailsAdapter
import kotlinx.android.synthetic.main.activity_account_details.*

class CompanyDetails : BaseActivity() {

    // Variables

    private lateinit var companyDetailModel: CompanyDetailsViewModel
    private lateinit var rpCompany: Company
    private lateinit var adapter: CompanyDetailsAdapter
    private lateinit var divider: DividerItemDecoration

    private val uriKimlicExplorer = BuildConfig.KIMLIC_EXPLORER_URI

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        companyDetailModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        val accountId = intent?.getStringExtra("accountId").orEmpty()

        initDivider()
        accountDetailsRecycler.addItemDecoration(divider)
        accountDetailsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = CompanyDetailsAdapter()
        accountDetailsRecycler.adapter = adapter


//        rpAccount = accountDetailModel.company(accountId = accountId.toLong())
//
//        with(rpAccount) {
//            titleTv.text = "KimlickRp"
//            subtitle1Tv.text = "subtitle Kimlick Rp"
//            Picasso.get...
        val detailsList: List<DetailsItem> = listOf(DateDetails(1538038182L), NameDetails("Mickle Sanders"), PhoneDetails("+38 (050)866-83-70"))
        adapter.setDetails(detailsList)

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
            showToast("Unlink Account")
        }
        backBt.setOnClickListener { finish() }
    }

    // Private

    private fun initDivider() {
        divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_line, null))
    }
}

interface DetailsItem {
    val type: String
    val value: String
}

class DateDetails(val date: Long) : DetailsItem {
    override val type: String get() = "date"
    override val value: String get() = date.toString()
}

class NameDetails(val name: String) : DetailsItem {
    override val type: String get() = "name"
    override val value: String get() = name
}

class PhoneDetails(val phone: String = "") : DetailsItem {
    override val type: String get() = "phone"
    override val value: String get() = phone
}