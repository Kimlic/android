package com.kimlic.stage

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
import com.kimlic.db.entity.Company
import com.kimlic.stage.adapter.CompanyDetailsAdapter
import com.kimlic.utils.svg.GlideApp
import com.kimlic.utils.svg.SvgSoftwareLayerSetter
import kotlinx.android.synthetic.main.activity_account_details.*

class CompanyDetails : BaseActivity() {

    // Variables

    private lateinit var companyDetailModel: CompanyDetailsViewModel
    private lateinit var adapter: CompanyDetailsAdapter
    private lateinit var divider: DividerItemDecoration
    private lateinit var company: Company

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
        val companyId = intent?.getStringExtra("companyId").orEmpty()

        initDivider()
        accountDetailsRecycler.addItemDecoration(divider)
        accountDetailsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = CompanyDetailsAdapter()
        accountDetailsRecycler.adapter = adapter

        company = companyDetailModel.company(companyId)

        setupDetails(company)

        viewExplorerBt.setOnClickListener {
            val kimlicPage = Uri.parse(uriKimlicExplorer)
            val intent = Intent(Intent.ACTION_VIEW, kimlicPage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
//        unlinkBt.setOnClickListener {
//            showToast("Unlink Account")
//        }
        backBt.setOnClickListener { finish() }
    }

    // Private

    private fun setupDetails(company: Company) {
        titleTv.text = company.name
        fillSubtitleBold()
        subtitle2Tv.text = getString(R.string.your_following_details_to_be_shared)

        //Utils.fetchSvg(this, company.logo, rpLogoIv)
        // Mock

        val detailsList: List<DetailsItem> = listOf(DateDetails(1538038182L), NameDetails("Mickle Sanders"), PhoneDetails("+38 (050)866-83-70"))
        adapter.setDetails(detailsList)

        GlideApp.with(this)
                .`as`(PictureDrawable::class.java)
                //.placeholder(R.drawable.image_loading)
                //.error(R.drawable.image_error)
                .transition(withCrossFade())
                .listener(SvgSoftwareLayerSetter())
                .load(company.logo)
                .into(rpLogoIv)
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