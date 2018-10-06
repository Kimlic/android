package com.kimlic.stage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.kimlic.BaseActivity
import com.kimlic.BuildConfig
import com.kimlic.R
import com.kimlic.model.ProfileViewModel
import com.kimlic.stage.adapter.CompanyDetailsAdapter
import com.kimlic.utils.svg.GlideApp
import com.kimlic.utils.svg.SvgSoftwareLayerSetter
import kotlinx.android.synthetic.main.activity_account_details.*

class CompanyDetails : BaseActivity() {

    // Variables

    private lateinit var companyModel: CompanyDetailsViewModel
    private lateinit var profileModel: ProfileViewModel
    private lateinit var adapter: CompanyDetailsAdapter
    private lateinit var divider: DividerItemDecoration

    private var dateDetails = DateDetails()
    private var nameDetails = NameDetails()
    private var phoneDetails = PhoneDetails()

    private val uriKimlicExplorer = BuildConfig.KIMLIC_EXPLORER_URI

    // Live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        companyModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)
        profileModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        val companyId = intent?.getStringExtra("companyId").orEmpty()

        initDivider()
        accountDetailsRecycler.addItemDecoration(divider)
        accountDetailsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = CompanyDetailsAdapter()
        accountDetailsRecycler.adapter = adapter

        fetchCompanyDetails(companyId)
        fetchCompanyDocumentDetails(companyId)
        fetchNameDetails()
        fetchPhoneDetails()

        backBt.setOnClickListener { finish() }

        viewExplorerBt.setOnClickListener {
            val kimlicPage = Uri.parse(uriKimlicExplorer)
            val intent = Intent(Intent.ACTION_VIEW, kimlicPage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    private fun fetchNameDetails() {
        profileModel.userLive().observe(this, Observer {
            nameDetails = NameDetails("${it!!.firstName}  ${it.lastName}")
            setupAdapterList()
        })
    }

    private fun fetchPhoneDetails() {
        profileModel.userContactsLive().observe(this, Observer {
            val phone = it!!.filter { it.type == "phone" }.first().value
            phoneDetails = PhoneDetails(phone = phone)
            setupAdapterList()
        })
    }

    private fun fetchCompanyDocumentDetails(companyId: String) {
        companyModel.companyDocumentDetails(companyId).observe(this, Observer { companyDocumentJoin ->
            dateDetails = DateDetails(companyDocumentJoin!!.date)
            setupAdapterList()
        })
    }

    private fun fetchCompanyDetails(companId: String) {
        companyModel.companyLive(companId).observe(this, Observer { company ->
            titleTv.text = company!!.name
            fillSubtitleBold()
            subtitle2Tv.text = getString(R.string.your_following_details_to_be_shared)

            GlideApp.with(this)
                    .`as`(PictureDrawable::class.java)
                    //.placeholder(R.drawable.image_loading)
                    //.error(R.drawable.image_error)
                    .transition(withCrossFade())
                    .listener(SvgSoftwareLayerSetter())
                    .load(company.logo)
                    .into(rpLogoIv)
        })
    }

    private fun setupAdapterList() {
        val detailsList: List<DetailsItem> = listOf(dateDetails, nameDetails, phoneDetails)
        adapter.setDetails(detailsList)
    }

    private fun fillSubtitleBold() {
        val spanText = getString(R.string.identity_verification_by_veriff)
        val words = spanText.split(" ")
        val spanStart = words[0].length + words[1].length + words[2].length + 3
        val spannableBuilder = SpannableStringBuilder(spanText)
        val boldStyle = StyleSpan(Typeface.BOLD)
        spannableBuilder.setSpan(boldStyle, spanStart, spanText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        subtitle1Tv.text = spannableBuilder
    }

    private fun initDivider() {
        divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_line, null))
    }
}

interface DetailsItem {
    val type: String
    val value: String
}

class DateDetails(val date: Long = 0) : DetailsItem {
    override val type: String get() = "date"
    override val value: String get() = date.toString()
}

class NameDetails(val name: String = "") : DetailsItem {
    override val type: String get() = "name"
    override val value: String get() = name
}

class PhoneDetails(val phone: String = "") : DetailsItem {
    override val type: String get() = "phone"
    override val value: String get() = phone
}