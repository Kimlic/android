package com.kimlic.vendors

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import android.widget.LinearLayout
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.stage.adapter.OnStageItemClick
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.activity_vendors.*

class VendorsActivity : BaseActivity() {

    // Variables

    private lateinit var vendorsModel: VendorsViewModel
    private lateinit var profileModel: ProfileViewModel
    private lateinit var documentAdapter: DocumentVendorAdapter
    private lateinit var country: String
    private lateinit var url: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendors)

        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        profileModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    // Private

    fun setupUI() {
        url = intent.extras.getString("path", "")
        initRecycler()

        lifecycle.addObserver(vendorsModel)

        vendorsModel.getDocumentsForAdapter().observe(this, Observer<List<Document>> { documents -> documentAdapter.setDocumentsList(documents = documents!!) })
        vendorsModel.vendorRequestStatus().observe(this, Observer<String> { errorPopup(it) })

        countryEt.setOnClickListener { initDropList() }
    }

    private fun initDropList() {
        val countries = vendorsModel.countries().map { it.country }.toList().toTypedArray()

        val dialog = AlertDialog.Builder(this)
                .setTitle("Countries")
                .setIcon(R.drawable.ic_kimlic_logo_with_text)
                .setItems(countries) { _, which ->
                    vendorsModel.supportedDocuments(country = vendorsModel.countries()[which].sh)
                    country = countries[which]
                    countryEt.text = Editable.Factory.getInstance().newEditable(countries[which])
                }.show()

        val rec = Rect()
        val window = this.window
        window.decorView.getWindowVisibleDisplayFrame(rec)
        dialog.window.setLayout((rec.width() * 0.9f).toInt(), (rec.height() * 0.7f).toInt())
    }

    private fun initRecycler() {
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_line, null))

        documentAdapter = DocumentVendorAdapter()

        with(documentRecycler) {
            layoutManager = LinearLayoutManager(this@VendorsActivity, LinearLayout.VERTICAL, false)
            adapter = documentAdapter
            addItemDecoration(divider)
        }

        documentAdapter.setOnStageItemClick(object : OnStageItemClick {
            override fun onClick(view: View, position: Int, type: String, approved: Boolean, state: String) {
                val presentDocList = profileModel.userDocuments().map { it.type }.toList()

                when (type) {
                    AppDoc.PASSPORT.type -> {
                        if (presentDocList.contains(AppDoc.PASSPORT.type))
                            PresentationManager.detailsDocumentSend(this@VendorsActivity, AppDoc.PASSPORT.type, url, country)
                        else PresentationManager.verifyDocument(this@VendorsActivity, AppDoc.PASSPORT.type)
                    }
                    AppDoc.ID_CARD.type -> {
                        if (presentDocList.contains(AppDoc.ID_CARD.type))
                            PresentationManager.detailsDocumentSend(this@VendorsActivity, AppDoc.ID_CARD.type, url, country)
                        else PresentationManager.verifyDocument(this@VendorsActivity, AppDoc.ID_CARD.type)
                    }
                    AppDoc.DRIVERS_LICENSE.type -> {
                        if (presentDocList.contains(AppDoc.DRIVERS_LICENSE.type))
                            PresentationManager.detailsDocumentSend(this@VendorsActivity, AppDoc.DRIVERS_LICENSE.type, url, country)
                        else PresentationManager.verifyDocument(this@VendorsActivity, AppDoc.DRIVERS_LICENSE.type)
                    }
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> {
                        if (presentDocList.contains(AppDoc.RESIDENCE_PERMIT_CARD.type))
                            PresentationManager.detailsDocumentSend(this@VendorsActivity, AppDoc.RESIDENCE_PERMIT_CARD.type, url, country)
                        else PresentationManager.verifyDocument(this@VendorsActivity, AppDoc.RESIDENCE_PERMIT_CARD.type)
                    }
                }
            }
        })
    }
}