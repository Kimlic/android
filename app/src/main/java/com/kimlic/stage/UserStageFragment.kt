package com.kimlic.stage

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
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
import com.kimlic.stage.adapter.OnUserItemClick
import com.kimlic.stage.adapter.UserStageAccountAdapter
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.fragment_stage_user.*
import java.io.File

class UserStageFragment : BaseFragment(), LifecycleObserver {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        private const val MAX_DOCUMENTS_COUNT = 6

        fun newInstance(bundle: Bundle = Bundle()): UserStageFragment {
            val fragment = UserStageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Variables

    private lateinit var divider: DividerItemDecoration
    private lateinit var adapter: UserStageAccountAdapter
    private var nameItem = NameItem(name = "")
    private var risksItem: RisksItem = RisksItem(false)
    private var kimItem = KimItem(quantity = 0)
    private var contactList = emptyList<ContactItem>()
    private var documentList = emptyList<DocumentItem>()
    private var addressItem = AddressItem(Address(value = ""))

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
        adapter = UserStageAccountAdapter()
        userStageRecycler.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        userStageRecycler.addItemDecoration(divider)
        userStageRecycler.adapter = adapter

        setupUser()
        setupContacts()
        setupAddress()
        setupDocuments()
        setupListeners()
    }

    private fun refreshList() {
        if (risksItem.present)
            adapter.setContacts(listOf(nameItem) + listOf(risksItem) + listOf(kimItem) + contactList + documentList + listOf(addressItem))
        else adapter.setContacts(listOf(nameItem) + listOf(kimItem) + contactList + documentList + listOf(addressItem))
    }

    // Private Helpers

    private fun manageRisks() {
        risksItem = if (Prefs.isPasscodeEnabled && Prefs.isRecoveryEnabled) RisksItem(false) else RisksItem(true)
    }

    private fun setupUser() {
        model.userLive().observe(this@UserStageFragment, Observer<User> { user ->
            user?.let {
                nameItem = if (it.firstName.isNotEmpty()) NameItem(name = "${it.firstName} ${it.lastName}") else NameItem(name = "")
                nameItem.isClickable = !model.hasDocumentInProgress()
                kimItem = KimItem(quantity = it.kimQuantity)
                refreshList()
            }
        })
        model.userPortraitPhotos().observe(this, Observer<List<Photo>> { list ->
            val photos = list?.map { it.type to it.file }!!.toMap()
            showPhoto(photos.getOrDefault("portrait_preview", ""))
            photos["portrait_preview"]?.let { manageCameraIcon(it) }
        })
    }

    private fun setupContacts() {
        model.userContactsLive().observe(this, Observer<List<Contact>> { contacts ->
            val tempList = mutableListOf(ContactItem(Contact(type = "phone")), ContactItem(Contact(type = "email")))
            contacts!!.forEach {
                if (it.type == "phone") tempList[0] = ContactItem(it)
                if (it.type == "email") tempList[1] = ContactItem(it)
            }
            contactList = tempList
            refreshList()
        })
    }

    private fun setupAddress() {
        model.userAddressLive().observe(this, Observer<Address> { address ->
            addressItem = AddressItem(address ?: Address(value = ""))
            refreshList()
        })
    }

    private fun setupDocuments() {
        model.userDocumentsLive().observe(this@UserStageFragment, Observer<List<Document>> { documents ->
            val docs = documents.orEmpty()
            val tempList = mutableListOf<DocumentItem>()
            val docTypes = AppDoc.values().map { it.type }

            docs.forEach {
                if (docTypes.contains(it.type)) {
                    tempList.add(DocumentItem(it))
                }
            }

            if (docs.size < MAX_DOCUMENTS_COUNT) tempList.add(DocumentItem(Document(type = "add")))

            documentList = tempList
            refreshList()
        })
    }

    private fun setupListeners() {
        settingsBt.setOnClickListener { PresentationManager.settings(activity!!) }
        takePhotoLl.setOnClickListener { PresentationManager.portraitPhoto(activity!!) }

        adapter.setOnUserItemClick(object : OnUserItemClick {
            override fun onItemClick(view: View, position: Int, type: String, value: String) {
                when (type) {
                    "USER_NAME" -> if (!model.hasDocumentInProgress()) PresentationManager.name(activity!!)
                    "phone" -> if (value == "") PresentationManager.phoneNumber(activity!!)
                    "email" -> if (value == "") PresentationManager.email(activity!!)
                    "address" -> PresentationManager.address(activity!!)
                    "risks" -> (getActivity() as StageActivity).risks()
                    "add" -> PresentationManager.documentChoiseVerify(activity!!)
                    "kim" -> model.tokensBalance()
                    else -> PresentationManager.detailsDocument(activity!!, type)
                }
            }
        })
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
        userPhoto.setOnClickListener { PresentationManager.portraitPhoto(activity!!) }
    }
}

interface UserItem {
    val type: String
    val value: String
    val tag: String
}

class NameItem(val name: String = "") : UserItem {
    override val tag: String get() = "name"
    override val type: String get() = "USER_NAME"
    override val value: String get() = name
    override fun toString(): String = this::class.java.simpleName
    var isClickable: Boolean = false
}

class RisksItem(val present: Boolean) : UserItem {
    override val tag: String get() = "risks"
    override val type: String get() = "risks"
    override val value: String get() = if (present) "1" else "0"
    override fun toString(): String = present.toString()
}

class KimItem(val quantity: Int = 0) : UserItem {
    override val tag: String get() = "kim"
    override val type: String get() = "kim"
    override val value: String get() = quantity.toString()
    override fun toString(): String = this::class.java.simpleName
}

class ContactItem(val contact: Contact) : UserItem {
    override val tag: String get() = contact.type
    override val type: String get() = contact.type
    override val value: String get() = contact.value
    override fun toString(): String = this::class.java.simpleName
}

class DocumentItem(val document: Document) : UserItem {
    override val tag: String get() = document.type
    override val type: String get() = document.type
    override val value: String get() = document.country
    override fun toString(): String = document.toString()
}

class AddressItem(val address: Address) : UserItem {
    override val tag: String get() = "address"
    override val type: String get() = "address"
    override val value: String get() = address.value
    override fun toString(): String = address.toString()
}