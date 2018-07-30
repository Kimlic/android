package com.kimlic.stage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.db.entity.*
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.ProfileViewModel
import com.kimlic.stage.adapter.ContactsAdapter
import com.kimlic.stage.adapter.DocumentAdapter
import com.kimlic.stage.adapter.Icons_
import com.kimlic.stage.adapter.OnStageItemClick
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_stage_user.*
import kotlinx.android.synthetic.main.item_stage.view.*
import java.io.File

class UserStageFragment : BaseFragment() {

    // Variables

    private lateinit var divider: DividerItemDecoration
    lateinit var contactsAdapter: ContactsAdapter
    lateinit var documentsAdapter: DocumentAdapter

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

    override fun onResume() {
        //checkPhotos()
//        Log.d("TAGACCOUNTADDRES", "account-addres = _" + Prefs.currentAccountAddress)
//        Log.d("TAGACCOUNTADDRES", "user address" + model.getUserAddress(Prefs.currentAccountAddress)?.toString())
//        Log.d("TAGACCOUNTADDRES", "user address photo"+ model.getUserAddressPhoto(Prefs.currentAccountAddress)?.toString())

        super.onResume()
    }

    // Private

    private fun setupUI() {
        initDivider()
        setupRisks()
        setupUser()
        setupContacts()
        setupAddress()
        setupDocuments()
        setupListners()
    }

    // Private Helpers

    private fun setupRisks() {
        model.getRisksLiveData()!!.observe(activity!!, object : Observer<Boolean> {
            override fun onChanged(risks: Boolean?) {
                manageRisks(risks!!)
            }
        })
    }

    private fun setupUser() {
        model.getUserLive(Prefs.currentAccountAddress).observe(this@UserStageFragment, object : Observer<User> {
            override fun onChanged(user: User?) {
                setupNameField(if (user!!.firstName.isNotEmpty()) {
                    "${user.firstName} ${user.lastName}"
                } else "")

                setupKimField(user.kimQuantity)
                showPhoto("preview_" + user.portraitFile)
                manageCameraIcon("preview_" + user.portraitFile)
            }
        })
    }

    private fun setupContacts() {
        contactsAdapter = ContactsAdapter()

        with(contactsRecycler) {
            layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            adapter = contactsAdapter
            addItemDecoration(divider)
        }

        contactsAdapter.setOnStageItemClick(object : OnStageItemClick {
            override fun onClick(view: View, position: Int, type: String, approved: Boolean, state: String) {
                when (type) {
                    "email" -> if (!approved) PresentationManager.email(activity!!)
                    "phone" -> if (!approved) PresentationManager.phoneNumber(activity!!)
                }
            }
        })

        model.getUserContactsLive(Prefs.currentAccountAddress).observe(this, object : Observer<List<Contact>> {
            override fun onChanged(contacts: List<Contact>?) {
                contactsAdapter.setContactsList(contacts = contacts!!)
            }
        })
    }

    private fun setupAddress() {
        model.getUserAddressesLive(Prefs.currentAccountAddress).observe(this, object : Observer<Address> {
            override fun onChanged(address: Address?) {
                setupAddressField(address?.value ?: "")
            }
        })
    }

    private fun setupDocuments() {
        documentsAdapter = DocumentAdapter()

        with(documentRecycler) {
            layoutManager = object : LinearLayoutManager(activity) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            adapter = documentsAdapter
            addItemDecoration(divider)
        }

        documentsAdapter.setOnStageItemClick(object : OnStageItemClick {
            override fun onClick(view: View, position: Int, type: String, approved: Boolean, state: String) {
                when (type) {
                    AppConstants.documentPassport.key -> PresentationManager.verifyDetails(activity!!, Prefs.currentAccountAddress, AppConstants.documentPassport.key)
                    AppConstants.documentLicense.key -> PresentationManager.verifyDetails(activity!!, Prefs.currentAccountAddress, AppConstants.documentLicense.key)
                    AppConstants.documentID.key -> PresentationManager.verifyDetails(activity!!, Prefs.currentAccountAddress, AppConstants.documentID.key)
                    AppConstants.documentPermit.key -> PresentationManager.verifyDetails(activity!!, Prefs.currentAccountAddress, AppConstants.documentPermit.key)
                    "add" -> PresentationManager.documentChooseVerify(activity!!)
                }
            }
        })

        model.getUserDocumentsLive(Prefs.currentAccountAddress).observe(this@UserStageFragment, object : Observer<List<Document>> {
            override fun onChanged(documents: List<Document>?) {
                documentsAdapter.setDocumentsList(documents!!)
            }
        })

    }

    private fun setupListners() {
        settingsBt.setOnClickListener { PresentationManager.settings(activity!!) }
        nameItem.setOnClickListener { PresentationManager.name(activity!!) }

        addressItem.setOnClickListener { PresentationManager.address(activity!!) }
        takePhotoLl.setOnClickListener {
            PresentationManager.portraitPhoto(activity!!)
//            it.visibility = if (setUserPhoto()) View.INVISIBLE else View.VISIBLE
        }
        // Stub?
        //userPhotoIv.setOnClickListener { PresentationManager.portraitPhoto(activity!!) }
    }

    private fun initDivider() {
        divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_line, null))
    }

    private fun manageCameraIcon(fileName: String) {
        if (File(activity!!.filesDir.toString() + "/" + fileName).exists()) takePhotoLl.visibility = View.GONE else takePhotoLl.visibility = View.VISIBLE
    }

    private fun manageRisks(value: Boolean) {
        risksTv.visibility = if (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled) View.GONE else View.VISIBLE
    }

    private fun showPhoto(fileName: String) {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER
        val userPhoto = UserPhotoView(activity!!, fileName)
        userPhoto.layoutParams = layoutParams
        userPhotoLl.removeAllViews()
        userPhotoLl.addView(userPhoto)
    }

    // Setup profile Fields

    private fun setupKimField(kim: Int = 0) {
        kimItem.iconIv.background = resources.getDrawable(if (kim == 0) Icons_.KIM_BLUE.icon else Icons_.KIM_WHITE.icon, null)
        kimItem.arrowIv.background = resources.getDrawable(if (kim == 0) Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
        kimItem.contentTv.text = Editable.Factory.getInstance().newEditable(getString(R.string.you_have_kim, kim))
        kimItem.contentTv.setTextColor(if (kim == 0) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

    private fun setupNameField(name: String = "") {
        nameArrow.background = resources.getDrawable(if (name.equals("")) Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
        nameTv.text = if (name.equals("")) getString(R.string.add_your_full_name) else name
        nameTv.setTextColor(if (name.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

    private fun setupAddressField(address: String = "") {
        addressItem.iconIv.background = resources.getDrawable(if (address.equals("")) Icons_.LOCATION_BLUE.icon else Icons_.LOCATION_WHITE.icon, null)
        addressItem.arrowIv.background = resources.getDrawable(if (address.equals("")) Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
        addressItem.contentTv.text = if (address.equals("")) getString(R.string.add_your_address) else address
        addressItem.contentTv.setTextColor(if (address.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }
}



