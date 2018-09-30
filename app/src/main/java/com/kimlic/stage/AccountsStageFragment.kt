package com.kimlic.stage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.account.CompanyDetailsViewModel
import com.kimlic.db.entity.Company
import com.kimlic.managers.PresentationManager
import com.kimlic.stage.adapter.CompanyAdapter
import com.kimlic.stage.adapter.OnAccountItemClick
import kotlinx.android.synthetic.main.fragment_stage_accounts.*

class AccountsStageFragment : BaseFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance(bundle: Bundle = Bundle()): AccountsStageFragment {
            val fragment = AccountsStageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var adapter: CompanyAdapter
    private var companiesList: List<Company> = emptyList()
    private lateinit var companyModel: CompanyDetailsViewModel

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stage_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        adapter = CompanyAdapter()
        adapter.setOnAccountItemClick(object : OnAccountItemClick {
            override fun onClick(position: Int) {
//                PresentationManager.companyDetails(activity!!, accountsList[position].id.toString())
                PresentationManager.companyDetails(activity!!, companiesList[position].id)
            }
        })

        recycler.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        recycler.adapter = adapter

//        val mockList = listOf(
//                Company(id = "1", name = "Kimlic"),
//                Company(id = "2", name = "Ebay"),
//                Company(id = "3", name = "Amazon"),
//                Company(id = "4", name = "EXMO"),
//                Company(id = "5", name = "Apple"),
//                Company(id = "6", name = "Samsung"),
//                Company(id = "7", name = "AWS"),
//                Company(id = "8", name = "Xiaomi"),
//                Company(id = "9", name = "Yobit"))
//                adapter.setAccountsList(mockList)

        companyModel.companiesLive().observe(this, Observer<List<Company>> { companyList ->
            companiesList = companyList.orEmpty()
            adapter.setAccountsList(companiesList)
        })

    }
}