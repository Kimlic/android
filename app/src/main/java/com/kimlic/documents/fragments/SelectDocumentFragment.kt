package com.kimlic.documents.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.documents.DocumentCallback
import com.kimlic.documents.DocumentViewModel
import com.kimlic.documents.adapter.DocumentAdapter
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.fragment_select_document.*

class SelectDocumentFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun getInstance(bundle: Bundle = Bundle()): SelectDocumentFragment {
            val fragment = SelectDocumentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var documentModel: DocumentViewModel
    private lateinit var callback: DocumentCallback
    private var previousPosition = -1
    private var chosenDocumentType: String = ""
    private var documents: List<Document> = listOf()

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_select_document, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentModel = ViewModelProviders.of(this).get(DocumentViewModel::class.java)
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
        val documentAdapter = DocumentAdapter()

        documentModel.documents().observe(this, Observer<List<Document>> { it ->
            documents = it!!
            val types = mutableListOf(AppDoc.PASSPORT.type, AppDoc.ID_CARD.type, AppDoc.DRIVERS_LICENSE.type, AppDoc.RESIDENCE_PERMIT_CARD.type, AppDoc.SOCIAL_SECURITY_CARD.type, AppDoc.BIRTH_CERTIFICATE.type)
            it.forEach { if (types.contains(it.type)) types.remove(it.type) }
            documentAdapter.setDocuments(types)
        })

        listView.adapter = documentAdapter
        listView.setOnItemClickListener { _, view, position, _ ->
            if (previousPosition == position) return@setOnItemClickListener
            else {
                documentAdapter.selectedPosition = position
                documentAdapter.notifyDataSetChanged()
            }
            previousPosition = position
            chosenDocumentType = view.tag.toString()
        }

        startBt.setOnClickListener { _ ->
            if (chosenDocumentType != "") {
                val bundle = Bundle()
                bundle.putString(AppConstants.documentType.key, chosenDocumentType)
                callback.callback(bundle)
            }
        }
    }
}