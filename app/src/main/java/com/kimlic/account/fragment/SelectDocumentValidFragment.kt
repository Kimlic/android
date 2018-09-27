package com.kimlic.account.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.account.SelectAccountDocumentViewModel
import com.kimlic.account.adapter.DocumentValidAdapter
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.VendorDocument
import com.kimlic.documents.DocumentViewModel
import com.kimlic.managers.PresentationManager
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.fragment_select_document_.*

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
    private lateinit var accountDocumentModel: SelectAccountDocumentViewModel

    private lateinit var vendorsDocumentsList: List<VendorDocument>
    private lateinit var userDocumentsList: List<Document>
    private lateinit var documentAccountAdapter: DocumentValidAdapter

    private var adaptersList: MutableList<ValidDocument> = mutableListOf()

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_select_document_, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountDocumentModel = ViewModelProviders.of(this).get(SelectAccountDocumentViewModel::class.java)
        documentModel = ViewModelProviders.of(this).get(DocumentViewModel::class.java)
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
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

        documentModel.documentsLive().observe(this, Observer<List<Document>> { userDocuments ->
            userDocumentsList = userDocuments.orEmpty()
            vendorsDocumentsList = vendorsModel.vendorsDocuments()
            adaptersList = mutableListOf()

            val vendorsList = vendorsDocumentsList.map { it.type to it.countries }.toMap()

            userDocumentsList.forEach {
                adaptersList.add(ValidDocument(it, it.type in vendorsList.keys))

            }
            documentAccountAdapter.setDocuments(adaptersList)
        })


        listView.setOnItemClickListener { _, view, position, _ ->
            if (adaptersList[position].isValid) PresentationManager.detailsDocumentChoose(activity!!, adaptersList[position].document.type, 4411)
        }

        addDocumentBt.setOnClickListener { _ ->
            PresentationManager.documentChoiseVerify(activity!!)
        }
    }

    // Valid DocumentClass

    class ValidDocument(val document: Document, val isValid: Boolean)
}