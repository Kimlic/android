package com.kimlic.account

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.account.adapter.RPAdapter
import com.kimlic.account.fragment.MissingInformationFragment
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.db.entity.VendorDocument
import com.kimlic.email.EmailActivity
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_account.*
import java.util.*

class AccountActivity : BaseActivity() {

    // Constants

    companion object {
        private const val EMAIL_VERIFY_REQUEST_CODE = 879
    }

    // Variables

    private lateinit var model: ProfileViewModel
    private lateinit var vendorsModel: VendorsViewModel
    private var nameItem: NameItem = NameItem("")
    private var contactList: MutableList<ContactItem> = mutableListOf()
    private var documentList: List<DocumentItem> = mutableListOf()

    private var timer: CountDownTimer? = null
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null

    private lateinit var url: String
    private lateinit var adapter: RPAdapter
    private lateinit var vendorsDocs: MutableMap<String, VendorDocument>
    private var missedName: Boolean = true
    private var missedContacts: Boolean = true
    private var missedDocuments: Boolean = true

    private lateinit var documentQueue: LinkedList<DocumentItem>
    private lateinit var vendorDocumentsList: List<VendorDocument>

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
        missingInfo(missedName || missedContacts || missedDocuments)
    }

    // Private

    private fun setupUI() {
        lifecycle.addObserver(vendorsModel)
        url = intent.extras.getString("path", "")

        val urltoParce = url.split("/")
        val urlnew = urltoParce[0] + "//" + urltoParce[1] + urltoParce[2]

        url = urlnew
        vendorsModel.RPDocuments(url)

        setupAdapter()
        setupAdapterList()
        setupAdapterListener()

        vendorsRequest()

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
        Log.d("TAGLIST", "adapters list ${listOf(nameItem) + contactList + documentList}")
    }

    private fun setupAdapterListener() {
        adapter.setOnStageItemClick(object : OnDocumentItemClick {
            override fun onItemClick(view: View, position: Int, type: String) {

                when (position) {
                    0 -> PresentationManager.name(this@AccountActivity)
                    1 -> PresentationManager.phoneNumber(this@AccountActivity)
                    2 -> {
                        val emailIntent = Intent(this@AccountActivity, EmailActivity::class.java)
                        startActivityForResult(emailIntent, EMAIL_VERIFY_REQUEST_CODE)
                    }
                    3, 4, 5, 6 -> PresentationManager.documentChooseVerify(this@AccountActivity)
                }
            }
        })
    }

    private fun vendorsRequest() {
        vendorsModel.vendorsDocumentsToVerify().observe(this, Observer<List<VendorDocument>> {
            Log.d("TAGVENDORS", "venorsList - $it")
            vendorsDocs = it!!.map { it.type to it }.toMap().toMutableMap()
            vendorDocumentsList = it
            setupName()
            setupDocuments()
            setupContacts()
            setupAdapterList()
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
        if (!missedDocs) {
            val missingFragment = MissingInformationFragment.getInstance()
            missingFragment.show(supportFragmentManager, MissingInformationFragment.FRAGMENT_KEY)
        }
    }

    private fun setupContacts() {
        model.userContactsLive().observe(this, Observer<List<Contact>> {
            val userContact = it?.orEmpty()
            val tempList = mutableListOf(ContactItem(Contact(type = "phone")), ContactItem(Contact(type = "email")))

            var count = 0

            userContact!!.forEach {
                if (it.type == "phone") tempList[0] = ContactItem(it)
                if (it.type == "email") {
                    tempList[1] = ContactItem(it); count++
                }
            }

            missedContacts = count != 1
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

            vendorsDocs.forEach {
                if (it.key in userDocumentsMap)
                    newList.add(DocumentItem(userDocumentsMap[it.key]!!))
                else {
                    count++; newList.add(DocumentItem(Document(type = it.key)))
                }
            }

            documentList = newList
            missedDocuments = count != 0
            setupAdapterList()
            setupNextButton()
        })
    }

    private fun setupNextButton() {
        if (missedName || missedContacts || missedDocuments) {
            acceptBt.isClickable = false
            acceptBt.isFocusableInTouchMode = false
        } else {
            acceptBt.setTextColor(Color.WHITE)
            acceptBt.setBackgroundResource(R.drawable.button_rounded_green_no_duration)
            acceptBt.setOnClickListener {
                if (documentList.isNotEmpty()) {
                    acceptBt.isClickable = false
                    showProgress()
                    documentQueue = LinkedList(documentList)

                    sendFromQueue(
                            onSuccess = {
                                hideProgress(); showPopup("Success", "Documents sent for verification"); Log.d("TAGS", "SUCCSESS")
                            },
                            onError = {
                                hideProgress(); errorShow(); Log.d("TAGS", "error")
                            })
                }
            }
        }
    }

    private fun sendFromQueue(onSuccess: () -> Unit, onError: () -> Unit) {
        documentQueue.poll()?.let { docItem ->
            val country = model.userDocuments().filter { it.type == docItem.type }.first().country

            val vendorDocument = vendorDocumentsList.filter { it.type == docItem.type }.first()
            model.senDoc(docItem.type, country, url, vendorDocument = vendorDocument,
                    onSuccess = {
                        Log.d("TAGSENDQUEUE", "success!!!! ${docItem.type}!!!!!!!!!!")
                        sendFromQueue({ onSuccess() }, { onError() })
                    },
                    onError = {
                        onError()
                        hideProgress()
                        Log.d("TAGSENDQUEUE", " (((((")
                    })
        } ?: onSuccess()
    }


    private fun showProgress() {
        timer = object : CountDownTimer(500, 500) {
            override fun onFinish() {
                blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
                blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer?.cancel()
    }

    fun errorShow() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
                .setMessage("Documents sending error")
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog?.dismiss(); finish() }.setCancelable(true)
                .setOnDismissListener(DialogInterface.OnDismissListener { finish() })
        val dialog = builder.create()
        dialog.show()
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