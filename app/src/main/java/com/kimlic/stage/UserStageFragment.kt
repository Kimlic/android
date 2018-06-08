package com.kimlic.stage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.fragment_stage_user.*

class UserStageFragment : BaseFragment() {

    // Variables

    private lateinit var uaserName: String
    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): UserStageFragment {
            val fragment = UserStageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stage_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    // Private

    private fun setupUI() {
        setupListners()
        setupTextFields()

    }

    private fun setupListners() {
        settingsBt.setOnClickListener { PresentationManager.settings(activity!!) }
        nameItem.setOnClickListener {
            PresentationManager.name(activity!!)
            showToast("name item is clicked")
        }
        phoneItem.setOnClickListener { showToast("phone item is clicked") }
        emailItem.setOnClickListener { showToast("email item is clicked") }
        verifyItem.setOnClickListener {
            PresentationManager.documentChooseVerify(activity!!)
            showToast("verify item is clicked") }
        addressItem.setOnClickListener { showToast("addres item is clicked") }
    }

    // Mocks

    private fun setupTextFields() {
        if (true) titleTv.text = "Add your name"
        if (true) subtitleTv.text = "ID assurance 15%"
        nameTv.text = "Your full name"
        phoneTv.text = "+1 199 99 9911"
        emailTv.text = "Add your email"
        verifyTv.text = "Verify your ID"
        addressTv.text = "Address"
    }
}
