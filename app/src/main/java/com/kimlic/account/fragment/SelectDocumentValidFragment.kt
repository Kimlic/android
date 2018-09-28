package com.kimlic.account.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.account.AccountActivity
import com.kimlic.account.SelectAccountDocumentViewModel
import com.kimlic.account.adapter.DocumentValidAdapter
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.VendorDocument
import com.kimlic.documents.DocumentViewModel
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppDoc
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.fragment_select_document_valid.*

class SelectDocumentValidFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun getInstance(bundle: Bundle = Bundle()): SelectDocumentValidFragment {
            val fragment = SelectDocumentValidFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var documentModel: DocumentViewModel
    private lateinit var vendorsModel: VendorsViewModel
    private lateinit var model: ProfileViewModel
    private lateinit var accountDocumentModel: SelectAccountDocumentViewModel

    private lateinit var vendorsDocumentsList: List<VendorDocument>
    //private lateinit var userDocumentsList: List<Document>
    private lateinit var documentAccountAdapter: DocumentValidAdapter

    //    private var adaptersList: MutableList<ValidDocument> = mutableListOf()
    private var adaptersList: MutableList<ValidDocument> = mutableListOf()

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_select_document_valid, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountDocumentModel = ViewModelProviders.of(this).get(SelectAccountDocumentViewModel::class.java)
        documentModel = ViewModelProviders.of(this).get(DocumentViewModel::class.java)
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(R.drawable.rounded_background_fragment_blue)
    }

    // Private

    private fun setupUI() {
        documentAccountAdapter = DocumentValidAdapter()
        listView.adapter = documentAccountAdapter

//        documentModel.documentsLive().observe(this, Observer<List<Document>> { userDocuments ->
//            userDocumentsList = userDocuments.orEmpty()
//            vendorsDocumentsList = vendorsModel.vendorsDocuments()
//            adaptersList = mutableListOf()
//
//            val sortedDocTypes = AppDoc.values().map { it.type }.toList()
//
//            val vendorsList = vendorsDocumentsList.map { it.type to it.countries }.toMap()

//            userDocumentsList.forEach {
//                adaptersList.add(ValidDocument(it, it.type in vendorsList.keys))
//            }
//            documentAccountAdapter.setDocuments(adaptersList)
//        })

        ViewModelProviders.of(this).get(ProfileViewModel::class.java).userDocumentsLive().observe(this, Observer<List<Document>> { userDocumentsList ->
            val userDocumentsMap = userDocumentsList?.map { it.type to it }?.toMap().orEmpty()
            val userDocumentsPresentTypes = userDocumentsList?.map { it.type }!!.toList()
            vendorsDocumentsList = vendorsModel.vendorsDocuments()

            val sortedDocTypes = AppDoc.values().map { it.type }.toList()
            val vendorTypes = vendorsDocumentsList.map { it.type }
            val vendorTypeContries = vendorsDocumentsList.map { it.type to it.countries }.toMap()

            adaptersList = mutableListOf()

            sortedDocTypes.forEach {
                if (it in userDocumentsPresentTypes) {
                    if (userDocumentsMap[it]!!.type in vendorTypes) {
                        if (model.userDocument(it)!!.countryIso.toUpperCase() in vendorTypeContries[it]!!) {
                            adaptersList.add(ValidDocument(model.userDocument(it)!!, true))
                        } else adaptersList.add(ValidDocument(model.userDocument(it)!!, false))
                    }
                }
            }
            documentAccountAdapter.setDocuments(adaptersList)

        })

        listView.setOnItemClickListener { _, view, position, _ ->
            // Previous flow
            //if (adaptersList[position].isValid) PresentationManager.detailsDocumentChoose(activity!!, adaptersList[position].document.type, 4411)

            if (adaptersList[position].isValid) {
                (activity as AccountActivity).setChosenDocument(adaptersList[position].document.type)
                dismiss()
            } //PresentationManager.detailsDocumentChoose(activity!!, adaptersList[position].document.type, 4411)
        }

        addDocumentBt.setOnClickListener { _ ->
            PresentationManager.documentChoiseVerify(activity!!)
        }
    }

    // Valid DocumentClass

    class ValidDocument(val document: Document, val isValid: Boolean)
}