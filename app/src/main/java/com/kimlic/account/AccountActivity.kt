package com.kimlic.account

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
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
    private lateinit var vendorsDocs: MutableMap<String, List<String>>
    private var missedName: Boolean = true
    private var missedContacts: Boolean = true
    private var missedDocuments: Boolean = true


    private lateinit var documentQueue: LinkedList<DocumentItem>
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
        setupAdapter()
        missingInfo(missedName || missedContacts || missedDocuments)
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

        vendorsModel.vendorsDocumentsToVerify().observe(this, Observer<List<VendorDocument>> {
            vendorsDocs = it!!.map { it.type to it.countries }.toMap().toMutableMap()

            setupName()
            setupDocuments()
            setupContacts()
            Log.d("TAGHASMISSED", "has missed fields = ${missingDoc()}")
            setupAdapter()
        })

        setupNextButton(missedName && missedContacts && missedDocuments)
        cancelTv.setOnClickListener { finish() }
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

    private fun setupNextButton(missedDocs: Boolean) {
        Log.d("TAGMISSED", "in button setup - ${!missedName || !missedContacts || !missedDocuments}")
        val bt = findViewById<Button>(R.id.aaaaaaBt)

        if (missedName || missedContacts || missedDocuments) {
            Log.d("TAGSETUPBUTTON", "in setup button missed docs = ${!missedName && !missedContacts && !missedDocuments}")
            aaaaaaBt.isClickable = false
            aaaaaaBt.isFocusableInTouchMode = false // true flow

            bt.setTextColor(Color.WHITE)
            bt.setBackgroundColor(Color.LTGRAY)// = resources.getDrawable(R.drawable.button_rounded_green, null)


        } else {//
            Log.d("TAGSETUPBUTTON", "in setup next button else")
//                    bt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_rounded_grey, 0,0,0,0)// = resources.getDrawable(R.drawable.button_rounded_grey, null)
            bt.setTextColor(Color.GRAY)
            bt.setBackgroundColor(Color.GREEN)
            aaaaaaBt.setOnClickListener {
                Log.d("TAGSEND", "documentList = $documentList")

                if (documentList.isNotEmpty())
                    showProgress()

                documentQueue = LinkedList(documentList)


                showProgress()
                sendFromQueue({ hideProgress(); Log.d("TAGS", "SUCCSESS") }, { Log.d("TAGS", "error") })


            }


        }
    }

    private fun sendFromQueue(onSuccess: () -> Unit, onError: () -> Unit) {

        documentQueue.poll()?.let { docItem ->
            val country = model.userDocuments().filter { it.type == docItem.type }.first().country
            model.senDoc(docItem.type, country, url, onSuccess = {
                hideProgress()
                Log.d("TAG", "${docItem.type}!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                sendFromQueue({}, {})
            }, onError = {
                onError()
                hideProgress()
                Log.d("TAG", "HUYNY)((((((((((((((((((((((((((((((((")
            })
        } ?: onSuccess()

    }

    private fun missingDoc(): Boolean = (missedName && missedContacts && missedDocuments)

    private fun missingInfo(missedDocs: Boolean) {
        if (missedDocs) {

            val missingFragment = MissingInformationFragment.getInstance()
            missingFragment.show(supportFragmentManager, MissingInformationFragment.FRAGMENT_KEY)
        }
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
            setupNextButton(missedName && missedContacts && missedDocuments)
        })
    }

    private fun setupContacts() {
        model.userContactsLive().observe(this, Observer<List<Contact>> {
            val userContact = it?.orEmpty()
            val tempList = mutableListOf(ContactItem(Contact(type = "phone")), ContactItem(Contact(type = "email")))

            var count = 0
            userContact!!.forEach {
                if (it.type == "phone") {
                    tempList[0] = ContactItem(it)
                }
                if (it.type == "email") {
                    tempList[1] = ContactItem(it); count += 1
                }
            }

            missedContacts = count != 1
            contactList = tempList
            setupAdapter()
            Log.d("TAGMISSED", "missed contact = $missedContacts")

            setupNextButton(missedName && missedContacts && missedDocuments)

        })
    }

    private fun setupDocuments() {
        model.userDocumentsLive().observe(this, Observer<List<Document>> {
            val newList = mutableListOf<DocumentItem>()
            val userDocumentsMap = it?.map { it.type to it }?.toMap().orEmpty()// документы, которые есть у пользователя


            //vendorsDocs.remove("PASSPORT") // vendorsDocs - документы, которые требует vendors
            //vendorsDocs.remove("ID_CARD")
            //vendorsDocs.remove("DRIVERS_LICENSE")
            //vendorsDocs.remove("RESIDENCE_PERMIT_CARD")

            var missed = 0
            vendorsDocs.forEach {
                if (it.key in userDocumentsMap) {
                    newList.add(DocumentItem(userDocumentsMap[it.key]!!))

                } else {
                    missed++
                    newList.add(DocumentItem(Document(type = it.key)))
                }
//                    missed++
//                newList.add(DocumentItem(Document(type = it.key)))
            }
            documentList = newList
            Log.d("TAGNEWLIST", "newList = $documentList")

            missedDocuments = missed != 0
            Log.d("TAGMISSED", "missed documents = $missedDocuments")

            setupAdapter()
            setupNextButton(missedName && missedContacts && missedDocuments)
        })
    }

    private fun setupAdapter() {
        adapter.setContacts(listOf(nameItem) + contactList + documentList)
        Log.d("TAGLIST", "adapters list ${listOf(nameItem) + contactList + documentList}")
    }

    private fun showProgress() {
        //aaaaaaBt.isClickable = false
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
        //aaaaaaBt.isClickable = true
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