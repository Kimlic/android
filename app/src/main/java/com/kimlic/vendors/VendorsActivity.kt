package com.kimlic.vendors

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.stage.adapter.OnStageItemClick
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.activity_vendors.*

class VendorsActivity : BaseActivity() {

    // Variables

    private lateinit var vendorsModel: VendorsViewModel
    private lateinit var profileModel: ProfileViewModel
    private lateinit var documentAdapter: DocumentVendorAdapter


    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendors)

        setupUI()
    }

    // Private

    fun setupUI() {
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        profileModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        initRecycler()

        lifecycle.addObserver(vendorsModel)

        vendorsModel.getVendors().observe(this, object : Observer<Vendors> {
            override fun onChanged(vendors: Vendors?) {
                Log.d("TAGVENDORS", "vendors object to string" + vendors?.toString())

                vendors?.documents!!.forEach {


                }
            }
        })

        vendorsModel.documentsForAdapter().observe(this, object : Observer<List<Document>> {
            override fun onChanged(documents: List<Document>?) {
                documentAdapter.setDocumentsList(documents = documents!!)
            }
        })

        vendorsModel.progress().observe(this, object : Observer<Boolean> {
            override fun onChanged(visible: Boolean?) {
                progressBar.visibility = if (!visible!!) View.GONE else View.VISIBLE
            }
        })



        countryEt.setOnClickListener { initDropList() }
    }

    private fun initDropList() {
        val countries = vendorsModel.countriesList().map { it.country }.toList().toTypedArray()

        val dialog = AlertDialog.Builder(this)
                .setTitle("Countries")
                .setIcon(R.drawable.ic_kimlic_logo_with_text)
                .setItems(countries, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        vendorsModel.getSupportedDocuments(country = vendorsModel.countriesList().get(which).sh)
                        countryEt.text = Editable.Factory.getInstance().newEditable(countries.get(which))
                    }
                }).show()
        val rec = Rect()
        val window = this.window
        window.decorView.getWindowVisibleDisplayFrame(rec)
        dialog.window.setLayout((rec.width() * 0.9f).toInt(), (rec.height() * 0.7f).toInt())
    }

    private fun initRecycler() {
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_line, null))

        documentAdapter = DocumentVendorAdapter()

//        documentAdapter = object: DocumentVendorAdapter(){
//            override fun setDocumentsList(documents: List<Document>) {
//
//
//
//            }
//        }

        with(documentRecycler) {
            layoutManager = LinearLayoutManager(this@VendorsActivity, LinearLayout.VERTICAL, false)
            adapter = documentAdapter
            addItemDecoration(divider)
        }

        documentAdapter.setOnStageItemClick(object : OnStageItemClick {
            override fun onClick(view: View, position: Int, type: String, approved: Boolean, state: String) {
                when (type) {


                    AppConstants.documentPassport.key -> PresentationManager.verifyDetails(this@VendorsActivity, Prefs.currentAccountAddress, AppConstants.documentPassport.key)
                    AppConstants.documentID.key -> PresentationManager.verifyDetails(this@VendorsActivity, Prefs.currentAccountAddress, AppConstants.documentID.key)
                    AppConstants.documentLicense.key -> PresentationManager.verifyDetails(this@VendorsActivity, Prefs.currentAccountAddress, AppConstants.documentLicense.key)
                    AppConstants.documentPermit.key -> PresentationManager.verifyDetails(this@VendorsActivity, Prefs.currentAccountAddress, AppConstants.documentPermit.key)
                }
            }
        })
    }
}
