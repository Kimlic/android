package com.kimlic.stage

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.db.entity.*
import com.kimlic.managers.PresentationManager
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.preferences.Prefs
import com.kimlic.stage.adapter.ContactsAdapter
import com.kimlic.stage.adapter.DocumentAdapter
import com.kimlic.stage.adapter.Icons_
import com.kimlic.stage.adapter.OnStageItemClick
import kotlinx.android.synthetic.main.fragment_stage_user.*
import kotlinx.android.synthetic.main.item_stage.view.*
import java.io.File

class UserStageFragment : BaseFragment(), LifecycleObserver {

    // Companion

    companion object {
        private const val SECURITY_REQUEST_CODE = 151

        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance(bundle: Bundle = Bundle()): UserStageFragment {
            val fragment = UserStageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var divider: DividerItemDecoration
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var documentsAdapter: DocumentAdapter

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stage_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        manageRisks()
    }

    // Private

    private fun setupUI() {
        initDivider()
        setupRisks()
        setupUser()
        setupContacts()
        setupAddress()
        setupDocuments()
        setupListeners()
    }

    // Private Helpers

    private fun setupRisks() {
        risksTv.setOnClickListener {
            (getActivity() as StageActivity).risks()
        }
    }

    private fun manageRisks() = (activity).let { risksTv?.visibility = if (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled) View.GONE else View.VISIBLE }

    private fun setupUser() {
        model.userLive().observe(this@UserStageFragment, Observer<User> { user ->
            user?.let {
                setupNameField(
                        if (it.firstName.isNotEmpty()) {
                            "${it.firstName} ${it.lastName}"
                        } else ""
                )
                setupKimField(it.kimQuantity)
            }
        })
        model.userPortraitPhotos().observe(this, Observer<List<Photo>> { list ->
            val photos = list?.map { it.type to it.file }!!.toMap()
            showPhoto(photos.getOrDefault("portrait_preview", ""))
            photos["portrait_preview"]?.let { manageCameraIcon(it) }
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

        model.userContactsLive().observe(this, Observer<List<Contact>> { contacts -> contactsAdapter.setContactsList(contacts = contacts!!) })
    }

    private fun setupAddress() {
        model.userAddressLive().observe(this, Observer<Address> { address -> setupAddressField(address?.value ?: "") })
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

                if (type == "add") PresentationManager.documentChooseVerify(activity!!)
                else PresentationManager.detailsDocument(activity!!, type)
            }
        })

        model.userDocumentsLive().observe(this@UserStageFragment, Observer<List<Document>> { documents -> documentsAdapter.setDocumentsList(documents!!) })
    }

    private fun setupListeners() {
        addressItem.setOnClickListener { PresentationManager.address(activity!!) }
        nameItem.setOnClickListener { if (!model.hasDocumentInProgress()) PresentationManager.name(activity!!) }
        settingsBt.setOnClickListener { PresentationManager.settings(activity!!) }
        takePhotoLl.setOnClickListener { PresentationManager.portraitPhoto(activity!!) }
        kimItem.setOnClickListener { model.tokensBalance() }
    }

    private fun initDivider() {
        divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_line, null))
    }

    private fun manageCameraIcon(fileName: String) {
        if (File(activity!!.filesDir.toString() + "/" + fileName).exists()) takePhotoLl.visibility = View.GONE else takePhotoLl.visibility = View.VISIBLE
    }

    private fun showPhoto(fileName: String) {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER
        val userPhoto = UserPhotoView(activity!!, fileName)
        userPhoto.layoutParams = layoutParams
        userPhotoLl.removeAllViews()
        userPhotoLl.addView(userPhoto)
    }

    private fun passcodeForResult() {
        val intent = Intent(activity, PasscodeActivity::class.java)
        intent.putExtra("action", "set")
        getActivity()!!.startActivityForResult(intent, SECURITY_REQUEST_CODE)
    }

    // Setup profile Fields

    private fun setupKimField(kim: Int = 0) {
        kimItem.iconIv.background = resources.getDrawable(if (kim == 0) Icons_.KIM_BLUE.icon else Icons_.KIM_WHITE.icon, null)
        kimItem.arrowIv.background = resources.getDrawable(if (kim == 0) Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
        kimItem.contentTv.text = Editable.Factory.getInstance().newEditable(getString(R.string.you_have_kim, kim))
        kimItem.contentTv.setTextColor(if (kim == 0) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

    private fun setupNameField(name: String = "") {
        nameArrow.background = resources.getDrawable(if (name == "") Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
        nameTv.text = if (name == "") getString(R.string.add_your_full_name) else name
        nameTv.setTextColor(if (name == "") resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }

    private fun setupAddressField(address: String = "") {
        addressItem.iconIv.background = resources.getDrawable(if (address == "") Icons_.LOCATION_BLUE.icon else Icons_.LOCATION_WHITE.icon, null)
        addressItem.arrowIv.background = resources.getDrawable(if (address == "") Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
        addressItem.contentTv.text = if (address == "") getString(R.string.add_your_address) else address
        addressItem.contentTv.setTextColor(if (address == "") resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
    }
}