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
import com.kimlic.account.AccountDetailsViewModel
import com.kimlic.db.entity.Account
import com.kimlic.managers.PresentationManager
import com.kimlic.stage.adapter.AccountAdapter
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

    private lateinit var adapter: AccountAdapter
    private var accountsList: List<Account> = emptyList()
    private lateinit var accountModel: AccountDetailsViewModel

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stage_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountModel = ViewModelProviders.of(this).get(AccountDetailsViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        adapter = AccountAdapter()
        adapter.setOnAccountItemClick(object : OnAccountItemClick {
            override fun onClick(position: Int) {
//                PresentationManager.accountDetails(activity!!, accountsList[position].id.toString())
                PresentationManager.accountDetails(activity!!, "1")
            }
        })

        recycler.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        recycler.adapter = adapter

        val mockList = listOf(
                Account(id = 1, name = "Kimlic"),
                Account(id = 2, name = "Ebay"),
                Account(id = 3, name = "Amazon"),
                Account(id = 4, name = "EXMO"),
                Account(id = 5, name = "Apple"),
                Account(id = 6, name = "Samsung"),
                Account(id = 8, name = "AWS"),
                Account(id = 9, name = "Xiaomi"),
                Account(id = 10, name = "Yobit"))



        accountModel.accountsLive().observe(this, Observer<List<Account>> {
            accountsList = it.orEmpty()
            //adapter.setAccountsList(mockList)
        })

        //adapter.setAccountsList(mockList)

    }
}