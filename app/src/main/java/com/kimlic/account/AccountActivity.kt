package com.kimlic.account

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.account.adapter.RPAdapter
import com.kimlic.account.fragment.IdentitySentSuccessfulFragment
import com.kimlic.account.fragment.MissingInformationFragment
import com.kimlic.account.fragment.SelectAccountDocumentFragment
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.db.entity.VendorDocument
import com.kimlic.documents.DocumentCallback
import com.kimlic.documents.fragments.SelectCountryFragment
import com.kimlic.documents.fragments.SelectDocumentFragment
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_account.*
import java.util.*

class AccountActivity : BaseActivity() {

    // Variables

    private lateinit var model: ProfileViewModel
    private lateinit var vendorsModel: VendorsViewModel

    private var nameItem: NameItem = NameItem("")
    private var contactList: MutableList<ContactItem> = mutableListOf()
    private var documentList: List<DocumentItem> = mutableListOf()
    private var timer: CountDownTimer? = null
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null
    private var missingFragment: MissingInformationFragment? = null

    private lateinit var chosenCountry: String

    private var missedName: Boolean = true
    private var missedContacts: Boolean = true
    private var missedDocuments: Boolean = true

    private lateinit var documentQueue: LinkedList<DocumentItem>
    private lateinit var vendorDocumentsList: List<VendorDocument>
    private lateinit var url: String
    private lateinit var adapter: RPAdapter
    private lateinit var vendorsDocs: MutableMap<String, VendorDocument>

    private lateinit var selectCountryFragment: SelectCountryFragment
    private lateinit var selectAccountDocumentFragment: SelectAccountDocumentFragment

    companion object {
        private const val DOCUMENT_VERIFY_REQUEST_CODE = 4444
    }

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
        setupAdapterList()
//        Handler().postDelayed({ missingInfo(missedName || missedContacts || missedDocuments) }, 1500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DOCUMENT_VERIFY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("TAGSELECTEDDOCUMENT", "on activity result")

            val acceptedDocumet = data?.getStringExtra(AppConstants.DOCUMENT_TYPE.key)
            Log.d("TAGSELECTEDDOCUMENT", "on activity result $acceptedDocumet")
            acceptedDocumet?.let {
                val document = model.userDocument(it)!!
                Log.d("TAGSELECTEDDOCUMENT", "in accepted - document = $document")
                documentList = listOf(DocumentItem(document))
                missedDocuments = false
                setupAdapterList()
                setupNextButton()
            }
        }
    }

    // Private

    private fun setupUI() {
        lifecycle.addObserver(vendorsModel)
        url = intent.extras.getString("path", "")

        val urlToParce = url.split("/")
        val urlNew = urlToParce[0] + "//" + urlToParce[1] + urlToParce[2]

        selectCountryFragment = SelectCountryFragment.getInstance()


        selectCountryFragment.setCallback(object : DocumentCallback {
            override fun callback(bundle: Bundle) {
                chosenCountry = bundle.getString(AppConstants.COUNTRY.key)

                showSelectDocumentFragment(chosenCountry = chosenCountry)
                Log.d("TAGDOCUMENT", "chosen country = $chosenCountry")
            }
        })


        url = urlNew
        vendorsModel.rpDocuments(url)// Request for RP documentsLive.

        setupAdapter()
        setupAdapterList()
        setupAdapterListener()

        fetchVendorDocs()

        setupNextButton()
        cancelTv.setOnClickListener { finish() }
    }

    private fun setupAdapter() {
        adapter = RPAdapter()
        contactRecycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        contactRecycler.adapter = adapter
    }

    private fun setupAdapterList() {
        adapter.setContacts(listOf(nameItem) + contactList + documentList)
    }

    private fun setupAdapterListener() {
        adapter.setOnStageItemClick(object : OnDocumentItemClick {
            override fun onItemClick(view: View, position: Int, type: String) {

                when (position) {
                    0 -> PresentationManager.name(this@AccountActivity)
                    1 -> PresentationManager.phoneNumber(this@AccountActivity)
                    //2 -> PresentationManager.email(this@AccountActivity)
//                    3, 4, 5, 6 -> PresentationManager.documentChoiseVerify(this@AccountActivity)
                    2 -> {
                        selectCountryFragment.show(supportFragmentManager, SelectCountryFragment.FRAGMENT_KEY)
                    }
                }
            }
        })
    }

    private fun showSelectDocumentFragment(chosenCountry: String) {
        val bundle = Bundle()
        bundle.putString(AppConstants.COUNTRY.key, chosenCountry)

        selectAccountDocumentFragment = SelectAccountDocumentFragment.getInstance(bundle)
        selectAccountDocumentFragment.setCallback(object : DocumentCallback {
            override fun callback(bundle: Bundle) {
                selectCountryFragment.dismiss()
                selectAccountDocumentFragment.dismiss()
                val documentToVerify = bundle.getString(AppConstants.DOCUMENT_TYPE.key, "")
                val action = bundle.getString("action", "")

                when (action) {
                    "apply" -> {
                        val document = model.userDocument(documentToVerify)!!
                        documentList = listOf(DocumentItem(document))
                        missedDocuments = false
                        setupAdapterList()
                        setupNextButton()
                    }
                    "add" -> {
                        PresentationManager.verifyDocument(this@AccountActivity, documentType = documentToVerify, country = chosenCountry, requestCode = DOCUMENT_VERIFY_REQUEST_CODE)
                    }
                }

                Log.d("TAGSELECTEDDOCUMENT", "selected document = ${documentToVerify}")
            }
        })
        selectAccountDocumentFragment.show(supportFragmentManager, SelectDocumentFragment.FRAGMENT_KEY)
    }

    private fun fetchVendorDocs() {
        vendorsModel.vendorsDocumentsToVerify().observe(this, Observer<List<VendorDocument>> { it ->
            vendorsDocs = it!!.map { it.type to it }.toMap().toMutableMap()
            vendorDocumentsList = it
            setupName()
            setupDocuments()
            setupContacts()
            setupAdapterList()
            Handler().postDelayed({ missingInfo(missedName || missedContacts || missedDocuments) }, 200)
        })
    }

    private fun setupName() {
        model.userLive().observe(this, Observer<User> { user ->
            nameItem = if (user?.firstName == "") {
                missedName = true; NameItem("")
            } else {
                missedName = false; user!!.let { NameItem(it.firstName + " " + it.lastName) }
            }
            setupAdapterList()
            setupNextButton()
        })
    }

    private fun missingInfo(missedDocs: Boolean) {
        if (missedDocs && missingFragment == null) {
            missingFragment = MissingInformationFragment.getInstance()
            missingFragment!!.show(supportFragmentManager, MissingInformationFragment.FRAGMENT_KEY)
        }
    }

    private fun setupContacts() {
        model.userContactsLive().observe(this, Observer<List<Contact>> {
            val userContact = it?.orEmpty()
            val tempList = mutableListOf(ContactItem(Contact(type = "phone")))//, ContactItem(Contact(type = "email")))

            var count = 0

            userContact!!.forEach { contact ->
                if (contact.type == "phone") tempList[0] = ContactItem(contact)
//                if (contact.type == "email") {
//                    tempList[1] = ContactItem(contact); count++
//                }
            }

            missedContacts = false//count != 1
            contactList = tempList
            setupAdapterList()
            setupNextButton()
        })
    }

    private fun setupDocuments() {


        model.userDocumentsLive().observe(this, Observer<List<Document>> { userDocumentsList ->
            val newList = mutableListOf<DocumentItem>()
            val userDocumentsMap = userDocumentsList?.map { it.type to it }?.toMap().orEmpty()
            //vendorsDocs.remove("PASSPORT")
            //vendorsDocs.remove("ID_CARD")
            //vendorsDocs.remove("DRIVERS_LICENSE")
            //vendorsDocs.remove("RESIDENCE_PERMIT_CARD")
            var count = 0
            // Добавить проверку по странам???
//            vendorsDocs.forEach {
//                if (it.key in userDocumentsMap) {
//                    newList.add(DocumentItem(userDocumentsMap[it.key]!!))
//                } else {
//                    count++; newList.add(DocumentItem(Document(type = it.key)))
//                }
//
//                if (it.key in userDocumentsMap) {
//                    if (!it.value.countries.contains(userDocumentsMap[it.key]?.countryIso?.toUpperCase())) {
//                        newList.removeAt(newList.lastIndex)
//                    }
//                }
//            }

//            newList.add(DocumentItem(Document(type = "addDocument")))
//            documentList = newList
//            missedDocuments = count != 0
//            setupAdapterList()
//            setupNextButton()
        })
        documentList = mutableListOf<DocumentItem>(DocumentItem(Document(type = "addDocument")))
        missedDocuments = true
        setupAdapterList()
        setupNextButton()
    }

    private fun setupNextButton() {
        if (missedName || missedContacts || missedDocuments) {
            acceptBt.isClickable = false
            acceptBt.isFocusableInTouchMode = false
            acceptBt.setBackgroundResource(R.drawable.button_rounded_grey_no_duration)
        } else {
            acceptBt.setTextColor(Color.WHITE)
            acceptBt.setBackgroundResource(R.drawable.button_rounded_green_no_duration)
            acceptBt.setOnClickListener {
                if (documentList.isNotEmpty()) {
                    acceptBt.isClickable = false
                    showProgress()
                    documentQueue = LinkedList(documentList)

                    sendFromQueue(
                            onSuccess = { hideProgress(); successful() },
                            onError = { hideProgress(); errorShow() })
                }
            }
        }
    }

    private fun sendFromQueue(onSuccess: () -> Unit, onError: () -> Unit) {
        documentQueue.poll()?.let { docItem ->
            val country = model.userDocuments().filter { it.type == docItem.type }.first().country

            val vendorDocument = vendorDocumentsList.filter { it.type == docItem.type }.first()

            // val urlHardcoded = "http://13.68.143.152/api/medias"
            model.senDoc(docItem.type, country, url, vendorDocument = vendorDocument,
//            model.senDoc(docItem.type, country, urlHardcoded, vendorDocument = vendorDocument,
                    onSuccess = {
                        sendFromQueue({ onSuccess() }, { onError() })
                    },
                    onError = { onError() })
        } ?: onSuccess()
    }

    private fun showProgress() {
        blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
        blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)

    }

    private fun hideProgress() {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss()

        timer?.cancel()
    }

    private fun errorShow() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.error))
                .setMessage(getString(R.string.documents_sending_error))
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog?.dismiss(); finish() }.setCancelable(true)
                .setOnDismissListener { finish() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun successful() {
        val fragment = IdentitySentSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
            }
        })
        fragment.show(supportFragmentManager, IdentitySentSuccessfulFragment.FRAGMENT_KEY)
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
    override val tag: String get() = "name"
    override val type: String get() = "USER_NAME"
    override val value: String get() = name
    override fun toString(): String = this::class.java.simpleName
}