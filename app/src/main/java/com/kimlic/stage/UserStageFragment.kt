package com.kimlic.stage

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.User
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.stage.adapter.ContactsAdapter
import com.kimlic.stage.adapter.OnStageItemClick
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_stage_user.*
import java.io.File

class UserStageFragment : BaseFragment() {

    // Variables

    private lateinit var model: UserStageViewModel
    private lateinit var risksLiveData: MutableLiveData<Boolean>

    // Variable fo adapters
    lateinit var contactsAdapter: ContactsAdapter

    // New DB implementation

    private lateinit var userLiveData: LiveData<User>

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
        model = ViewModelProviders.of(activity!!).get(UserStageViewModel::class.java)

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_line, null))

        setupContacts()


        model.getUserContactLiveData().observe(this, object : Observer<List<Contact>> {
            override fun onChanged(contacts: List<Contact>?) {
                contactsAdapter.setContactsList(contacts = contacts!!)
            }

        })

        // New Db implementation
        userLiveData = model.getUser1LiveData()
        userLiveData.observe(this@UserStageFragment, object : Observer<User> {
            override fun onChanged(user: User?) {
                setupNameField(if (user!!.firstName.isNotEmpty()) {
                    user.firstName + " " + user.lastName
                } else "")

                showPhoto("preview_" + user.portraitFile)
                manageCameraIcon("preview_" + user.portraitFile)
            }
        })

        // UserContacts
        model.getUserContactLiveData().observe(this@UserStageFragment, object : Observer<List<Contact>> {
            override fun onChanged(contactList: List<Contact>?) {
                contactsAdapter.setContactsList(contactList!!)
            }
        })


        // Risks liveData
        risksLiveData = model.getRisksLiveData()
        risksLiveData.observe(activity!!, object : Observer<Boolean> {
            override fun onChanged(risks: Boolean?) {
                manageRisks(risks!!)
            }
        })

        setupListners()
        setupFielsds()
    }

    private fun setupContacts() {
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_line, null))

        contactsAdapter = ContactsAdapter()
        val layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        contactsRecycler.layoutManager = layoutManager
        contactsRecycler.hasFixedSize()
        contactsRecycler.adapter = contactsAdapter
        contactsRecycler.addItemDecoration(dividerItemDecoration)
        contactsAdapter.onStageItemClick = object : OnStageItemClick {
            override fun onClick(view: View, position: Int, type: String, aprooved: Boolean) {
                when (type) {
                    "email" -> {

                        Log.d("TAG", type)
                        PresentationManager.email(activity!!)
                    }

                }
            }
        }
    }

    private fun manageCameraIcon(fileName: String) {
        if (File(activity!!.filesDir.toString() + "/" + fileName).exists()) takePhotoLl.visibility = View.GONE else takePhotoLl.visibility = View.VISIBLE
    }

    private fun setupListners() {
        settingsBt.setOnClickListener { PresentationManager.settings(activity!!) }
        nameItem.setOnClickListener { PresentationManager.name(activity!!) }

        idItem.setOnClickListener { PresentationManager.documentChooseVerify(activity!!) }
        addressItem.setOnClickListener { PresentationManager.address(activity!!) }
        takePhotoLl.setOnClickListener {
            PresentationManager.portraitPhoto(activity!!)
//            it.visibility = if (setUserPhoto()) View.INVISIBLE else View.VISIBLE
        }
        // Stub?
        //userPhotoIv.setOnClickListener { PresentationManager.portraitPhoto(activity!!) }
    }

    // Mocks

    private fun manageRisks(value: Boolean) {
        risksTv.visibility = if (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled) View.GONE else View.VISIBLE
    }

    private fun setupFielsds() {
        setupKimField(2)

        when (Prefs.documentToverify) {
            AppConstants.documentPassport.key -> {
                setupIDField(getString(R.string.passport)); idItem.setOnClickListener { PresentationManager.verifyDetails(activity!!, AppConstants.documentPassport.key) }
            }
            AppConstants.documentLicense.key -> {
                setupIDField(getString(R.string.driver_licence)); idItem.setOnClickListener {
                    PresentationManager.verifyDetails(activity!!, AppConstants.documentLicense.key)
                }
            }
            AppConstants.documentID.key -> {
                setupIDField(getString(R.string.id_card)); idItem.setOnClickListener { PresentationManager.verifyDetails(activity!!, AppConstants.documentID.key) }
            }
            else -> {
                setupIDField(""); idItem.setOnClickListener { PresentationManager.documentChooseVerify(activity!!) }
            }
        }
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
        kimIv.background = resources.getDrawable(if (kim == 0) Icons.KIM_BLUE.icon else Icons.KIM_WHITE.icon, null)
        kimArrow.background = resources.getDrawable(if (kim == 0) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        kimTv.text = Editable.Factory.getInstance().newEditable(getString(R.string.you_have_kim, kim))
        kimTv.setTextColor(if (kim == 0) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

//    private fun setupEmailField(email: String = "") {
//        emailIv.background = resources.getDrawable(if (email.equals("")) Icons.EMAIL_BLUE.icon else Icons.EMAIL_WHITE.icon, null)
//        emailArrow.background = resources.getDrawable(if (email.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
//        emailTv.text = if (email.equals("")) getString(R.string.add_your_email) else email
//        emailTv.setTextColor(if (email.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
//        emailItem.setOnClickListener { PresentationManager.email(activity!!) }
//        emailItem.isClickable = email.equals("")
//    }

    private fun setupNameField(name: String = "") {
//        nameIv.background = resources.getDrawable(if (name.equals("")) Icons.NAME_BLUE.icon else Icons.NAME_WHITE.icon, null)
        nameArrow.background = resources.getDrawable(if (name.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        nameTv.text = if (name.equals("")) getString(R.string.add_your_full_name) else name
        nameTv.setTextColor(if (name.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

//    private fun setupPhoneField(phone: String = "") {
//        phoneIv.background = resources.getDrawable(if (phone.equals("")) Icons.PHONE_BLUE.icon else Icons.PHONE_WHITE.icon, null)
//        phoneArrow.background = resources.getDrawable(if (phone.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
//        phoneTv.text = if (phone.equals("")) getString(R.string.add_your_phone) else phone
//        phoneTv.setTextColor(if (phone.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
//        phoneItem.setOnClickListener { PresentationManager.phoneNumber(activity!!) }
//        phoneItem.isClickable = phone.equals("")
//    }

    private fun setupIDField(id: String = "") {
        idIv.background = resources.getDrawable(if (id.equals("")) Icons.ID_BLUE.icon else Icons.ID_WHITE.icon, null)
        idArrow.background = resources.getDrawable(if (id.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        idTv.text = if (id.equals("")) getString(R.string.verify_your_id) else id
        idTv.setTextColor(if (id.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

    private fun setupAddressField(address: String = "") {
        addressIv.background = resources.getDrawable(if (address.equals("")) Icons.LOCATION_BLUE.icon else Icons.LOCATION_WHITE.icon, null)
        addressArrow.background = resources.getDrawable(if (address.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        addressTv.text = if (address.equals("")) getString(R.string.add_your_address) else address
        addressTv.setTextColor(if (address.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }
}

internal enum class Icons(val icon: Int) {

    KIM_BLUE(R.drawable.ic_profile_name_icon_blue),
    KIM_WHITE(R.drawable.ic_profile_name_icon_white),

    NAME_BLUE(R.drawable.ic_profile_name_icon_blue),
    NAME_WHITE(R.drawable.ic_profile_name_icon_white),

    PHONE_BLUE(R.drawable.ic_profile_phone_icon_blue),
    PHONE_WHITE(R.drawable.ic_profile_phone_icon_white),

    EMAIL_BLUE(R.drawable.ic_profile_email_icon_blue),
    EMAIL_WHITE(R.drawable.ic_profile_email_icon_white),

    ID_BLUE(R.drawable.ic_profile_id_icon_blue),
    ID_WHITE(R.drawable.ic_profile_id_icon_white),

    LOCATION_BLUE(R.drawable.ic_profile_blue_location_icon),
    LOCATION_WHITE(R.drawable.ic_profile_white_location_icon),

    ARROW_BLUE(R.drawable.ic_blue_menu_arrow),
    ARROW_WHITE(R.drawable.ic_white_menu_arrow)
}



