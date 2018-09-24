package com.kimlic.account.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.account.SelectAccountDocumentViewModel
import com.kimlic.account.adapter.DocumentAccountAdapter
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.VendorDocument
import com.kimlic.documents.DocumentCallback
import com.kimlic.documents.DocumentViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.fragment_select_document.*

class SelectAccountDocumentFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun getInstance(bundle: Bundle = Bundle()): SelectAccountDocumentFragment {
            val fragment = SelectAccountDocumentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var documentModel: DocumentViewModel
    private lateinit var vendorsModel: VendorsViewModel

    private lateinit var callback: DocumentCallback
    private var previousPosition = -1
    private var chosenDocumentType: String = ""
    private var documents: List<Document> = listOf()

    private lateinit var chosenCountry: String
    private lateinit var accountDocumentModel: SelectAccountDocumentViewModel
    private lateinit var vendorsDocumentsList: List<VendorDocument>

    private lateinit var documentAccountAdapter: DocumentAccountAdapter


    private lateinit var adaptersList: List<Document>

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_select_document, container, false)


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

    // Public

    fun setCallback(callback: DocumentCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        chosenCountry = arguments!!.getString(AppConstants.COUNTRY.key)
        val chosenCountrySH = vendorsModel.countries().filter { it.country == chosenCountry }.first().sh.toUpperCase()

        documentAccountAdapter = DocumentAccountAdapter()

        documents = documentModel.documents()// Список документов, которые есть у пользователя.

        accountDocumentModel.vendorsDocumentsLive().observe(this, Observer<List<VendorDocument>> { it ->
            vendorsDocumentsList = it.orEmpty()

            val vendorsDocs = it!!.map { it.type to it }.toMap().toMutableMap()
            val newList = mutableListOf<Document>()


            Log.d("TAG", "vendorsDocuments ")
            val userDocuments = documentModel.documents()// Cписок документов пользователя
            val userDocumentsMap = userDocuments.map { it.type to it }.toMap().orEmpty()

            vendorsDocs.forEach { vendorDocument ->
                if ((vendorDocument.key in userDocumentsMap) && vendorDocument.value.countries.contains(chosenCountrySH)) {
                    newList.add(userDocumentsMap[vendorDocument.key]!!)

                    if (userDocumentsMap[vendorDocument.key]?.countryIso != chosenCountrySH)
                        newList.removeAt(newList.lastIndex)
                } else {
                    if (vendorDocument.value.countries.contains(chosenCountrySH))
                        newList.add(Document(type = vendorDocument.key))
                }
            }

            Log.d("TAGDOCUMENTS", " doccuments to adapter ${newList}")
            adaptersList = newList
            documentAccountAdapter.setDocuments(adaptersList)
        })


        listView.adapter = documentAccountAdapter
        listView.setOnItemClickListener { _, view, position, _ ->
            if (previousPosition == position) return@setOnItemClickListener
            else {
                documentAccountAdapter.selectedPosition = position
                documentAccountAdapter.notifyDataSetChanged()
            }
            previousPosition = position
        }

        startBt.setOnClickListener { _ ->
            val bundle = Bundle()
            if(previousPosition<0) activity!!.finish()// TODO check this

            if (adaptersList[previousPosition].number != "") {
                bundle.putString("action", "apply")
                bundle.putString(AppConstants.DOCUMENT_TYPE.key, adaptersList[previousPosition].type)
                callback.callback(bundle)
            } else {
                bundle.putString("action", "add")
                bundle.putString(AppConstants.DOCUMENT_TYPE.key, adaptersList[previousPosition].type)
                callback.callback(bundle)
            }
        }
    }
}