package com.kimlic.documents.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.documents.DocumentCallback
import com.kimlic.documents.DocumentVerifyChooseActivity
import com.kimlic.documents.DocumentViewModel
import com.kimlic.documents.adapter.CountryAdapter
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_select_country.*

class SelectCountryFragment : BasePopupFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun getInstance(bundle: Bundle? = Bundle()): SelectCountryFragment {
            val fragment = SelectCountryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var model: DocumentViewModel
    private lateinit var callback: DocumentCallback
    private lateinit var countries: List<String>
    private var chosenCountry: String = ""
    private var previousPosition: Int = -1

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_select_country, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProviders.of(this).get(DocumentViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(R.drawable.rounded_background_fragment_blue)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        (activity as DocumentVerifyChooseActivity).finish()
        super.onDismiss(dialog)
    }

    // Public

    fun setCallback(callback: DocumentCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        countries = model.countries().map { it.country }
        val adapter = CountryAdapter()
        adapter.setCountries(countries)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            if (previousPosition == position) return@setOnItemClickListener
            else {
                adapter.selectedPosition = position
                adapter.notifyDataSetChanged()
            }
            chosenCountry = countries[position]
            previousPosition = position
        }

        continueBt.setOnClickListener {
            if (chosenCountry == "") return@setOnClickListener

            val bundle = Bundle()
            bundle.putString(AppConstants.COUNTRY.key, chosenCountry)
            callback.callback(bundle)
        }
    }
}