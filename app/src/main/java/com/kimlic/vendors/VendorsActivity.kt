package com.kimlic.vendors

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kimlic.BaseActivity
import com.kimlic.R
import kotlinx.android.synthetic.main.activity_vendors.*

class VendorsActivity : BaseActivity() {

    private lateinit var vendorsModel: VendorsViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendors)

        setupUI()
    }

    // Private

    fun setupUI() {
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        lifecycle.addObserver(vendorsModel)

        vendorsModel.getVendors().observe(this, object : Observer<Vendors> {
            override fun onChanged(vendors: Vendors?) {
                Log.d("TAGVENDORS", "vendors object to string" + vendors?.toString())
                vendors?.documents!!.forEach {


                }
            }
        })

        vendorsModel.progress().observe(this, object : Observer<Boolean> {
            override fun onChanged(visible: Boolean?) {
                progressBar.visibility = if (!visible!!) View.GONE else View.VISIBLE
            }
        })
    }
}
