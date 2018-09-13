package com.kimlic.account

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.account.adapter.RPAdapter
import com.kimlic.account.fragment.MissingInformationFragment
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.db.entity.VendorDocument
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : BaseActivity() {

    // Variables

    private lateinit var model: ProfileViewModel
    private lateinit var vendorsModel: VendorsViewModel
    private var nameItem: NameItem = NameItem("")
    private var contactList: MutableList<ContactItem> = mutableListOf()
    private var documentList: List<DocumentItem> = mutableListOf()
    private lateinit var url: String
    private lateinit var adapter: RPAdapter

    private var missedName: Boolean = true
    private var missedContacts: Boolean = true
    private var missedDocuments: Boolean = true

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        missingInfo(missedDocuments)
    }

    // Private

    private fun setupUI() {
        lifecycle.addObserver(vendorsModel)
        url = intent.extras.getString("path", "")
        vendorsModel.RPDocuments(url)


        adapter = RPAdapter()
        contactRecycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        contactRecycler.adapter = adapter
        setupAdapterListener()


        val vendorsMockList = listOf(VendorDocument(type = "PASSPORT"), VendorDocument(type = "ID_CARD"), VendorDocument(type = "DRIVERS_LICENSE"), VendorDocument(type = "RESIDENCE_PERMIT_CARD"))
        // vendorsModel.vendorsDocumentsToVerify().observe(this, Observer<List<VendorDocument>> {


//            val docs = it!!.map { it.type to it.countries }.toMap().toMutableMap()
        val docs = vendorsMockList!!.map { it.type to it.countries }.toMap().toMutableMap()
//            docs.remove("PASSPORT");docs.remove("ID_CARD")
//            docs.remove("DRIVERS_LICENSE");docs.remove("RESIDENCE_PERMIT_CARD")

        val userDocumentsMap = model.userDocuments().map { it.type to it }.toMap()
        val newList = mutableListOf<DocumentItem>()

        docs.forEach {
            if (it.key in userDocumentsMap) {
                newList.add(DocumentItem(userDocumentsMap[it.key]!!))
            } else
                newList.add(DocumentItem(Document(type = it.key)))
        }

        documentList = newList

        missedDocuments = false
        setupName()
        setupContacts()
        Log.d("TAGHASMISSED", "has missed fields = ${misedDoc()}")
        setupAdapter()
        //})


        cancelTv.setOnClickListener { finish() }
        setupNextButton()
    }

    private fun setupAdapterListener() {
        adapter.setOnStageItemClick(object : OnDocumentItemClick {
            override fun onItemClick(view: View, position: Int, type: String) {


                when (position) {
                    0 -> PresentationManager.name(this@AccountActivity)
                    1 -> PresentationManager.phoneNumber(this@AccountActivity)
                    2 -> PresentationManager.email(this@AccountActivity)
                    3, 4, 5, 6 -> PresentationManager.documentChooseVerify(this@AccountActivity)
                }
            }
        })
    }

    private fun setupNextButton() {

        with(createBt) {
            if (misedDoc()) {
                isClickable = false
                background = resources.getDrawable(R.drawable.button_rounded_grey, null)
                setTextColor(Color.GRAY)
            } else {
                isClickable = true
                background = resources.getDrawable(R.drawable.button_rounded_green, null)
                setTextColor(resources.getColor(R.color.grayDark))
            }
        }

        createBt.setOnClickListener {
            Log.d("TAGSEND", "documentList = $documentList")


        }


    }

    private fun misedDoc(): Boolean = (missedName && missedContacts && missedDocuments)

    private fun missingInfo(missedDocs: Boolean) {
        if (missedDocs) {
            val missingFragment = MissingInformationFragment.getInstance()
            missingFragment.show(supportFragmentManager, MissingInformationFragment.FRAGMENT_KEY)
        }
    }

    private fun setupContacts() {
        model.userContactsLive().observe(this, Observer<List<Contact>> {
            val userContact = it?.orEmpty()
            val tempList = mutableListOf(ContactItem(Contact(type = "phone")), ContactItem(Contact(type = "email")))
            userContact!!.forEach {
                if (it.type == "phone") tempList[0] = ContactItem(it)
                if (it.type == "email") tempList[1] = ContactItem(it)
            }
            contactList = tempList
            setupAdapter()
            Log.d("TAGMISSED", "missed contact = $missedName")

        })
    }

    private fun setupName() {
        model.userLive().observe(this, Observer<User> {
            nameItem = if (it?.firstName == "") {
                missedName = true; NameItem("")
            } else {
                missedName = false; it!!.let { NameItem(it.firstName + " " + it.lastName) }
            }
            Log.d("TAGMISSED", "missed name = $missedName")
            setupAdapter()
        })
    }

    private fun setupAdapter() {
        adapter.setContacts(listOf(nameItem) + contactList + documentList)
        Log.d("TAGLIST", "adapters list ${listOf(nameItem) + contactList + documentList}")
    }

}

interface AccountItem {
    val type: String
    val value: String
    val tag: String
}

class ContactItem(val contact: Contact) : AccountItem {
    override val tag: String get() = contact.type
    override val type: String get() = contact.type
    override val value: String get() = contact.value
    override fun toString(): String = this::class.java.simpleName
}

class DocumentItem(val document: Document) : AccountItem {
    override val tag: String get() = document.type
    override val type: String get() = document.type
    override val value: String get() = document.country
    override fun toString(): String = document.toString()
}

class NameItem(val name: String = "") : AccountItem {
    override val tag: String
        get() = "name"
    override val type: String
        get() = "USER_NAME"
    override val value: String
        get() = name

    override fun toString(): String = this::class.java.simpleName
}