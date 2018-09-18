package com.kimlic.stage

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kimlic.BaseFragment
import com.kimlic.R
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

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stage_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    // Private

    private fun setupUI() {
        adapter = AccountAdapter()
        adapter.setOnAccountItemClick(object : OnAccountItemClick {
            override fun onClick(position: Int) {
                Log.d("TAGCLICK", "position ++")
            }
        })

        recycler.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        recycler.adapter = adapter

        val mockList = listOf("Kimlic", "Ebay", "Amazon", "EXMP", "BP", "AWS", "MacBookPro", "Xiaomi")
        //adapter.setAccountsList(mockList)

    }
}