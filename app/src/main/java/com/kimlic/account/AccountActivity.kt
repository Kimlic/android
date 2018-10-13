package com.kimlic.account

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.account.adapter.RPAdapter
import com.kimlic.account.fragment.IdentitySentSuccessfulFragment
import com.kimlic.account.fragment.MissingInformationFragment
import com.kimlic.account.fragment.SelectAccountDocumentFragment
import com.kimlic.account.fragment.SelectDocumentValidFragment
import com.kimlic.db.entity.*
import com.kimlic.documents.DocumentCallback
import com.kimlic.documents.Status
import com.kimlic.documents.fragments.SelectCountryFragment
import com.kimlic.documents.fragments.SelectDocumentFragment
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.stage.CompanyDetailsViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.AppDoc
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.svg.GlideApp
import com.kimlic.utils.svg.SvgSoftwareLayerSetter
import com.kimlic.vendors.VendorsViewModel
import kotlinx.android.synthetic.main.activity_account.*
import java.util.*

class AccountActivity : BaseActivity() {

    // Constants

    companion object {
        private const val DOCUMENT_VERIFY_REQUEST_CODE = 4444
        const val DOCUMENT_DETAILS_CHOOSE_CODE = 4411
    }

    // Variables

    private lateinit var model: ProfileViewModel
    private lateinit var vendorsModel: VendorsViewModel
    private lateinit var companyModel: CompanyDetailsViewModel

    private var nameItem: NameItem = NameItem("")
    private var contactList: MutableList<ContactItem> = mutableListOf()
    private var documentList: List<DocumentItem> = mutableListOf()
    private var timer: CountDownTimer? = null
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null
    private var missingFragment: MissingInformationFragment? = null

    private var missedName: Boolean = true
    private var missedContacts: Boolean = true
    private var missedDocuments: Boolean = true

    private var chosenCountry = "Ukraine"

    private lateinit var documentQueue: LinkedList<DocumentItem>
    private lateinit var vendorDocumentsList: List<VendorDocument>
    private lateinit var url: String
    private var adapter: RPAdapter? = null
    private lateinit var vendorsDocs: MutableMap<String, VendorDocument>
    private var currentCompany: Company? = null

    private lateinit var selectCountryFragment: SelectCountryFragment
    private lateinit var selectAccountDocumentFragment_: SelectDocumentValidFragment
    private lateinit var selectAccountDocumentFragment: SelectAccountDocumentFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        vendorsModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        companyModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        //setupAdapterList()
        //Handler().postDelayed({ missingInfo(missedName || missedContacts || missedDocuments) }, 1500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DOCUMENT_VERIFY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val acceptedDocument = data?.getStringExtra(AppConstants.DOCUMENT_TYPE.key)
            acceptedDocument?.let {
                val document = model.userDocument(it)!!
                documentList = listOf(DocumentItem(document))
                missedDocuments = false
                setupAdapterList()
                setupNextButton()
            }
        }

        if (requestCode == DOCUMENT_DETAILS_CHOOSE_CODE && resultCode == Activity.RESULT_OK) {
            val documentType = data?.getStringExtra(AppConstants.DOCUMENT_TYPE.key)!!
            val documentToVerify = model.userDocument(documentType = documentType)!!
            selectAccountDocumentFragment_.dismiss()
            documentList = listOf(DocumentItem(documentToVerify))

            missedDocuments = false
            setupAdapterList()
            setupNextButton()
        }
    }

    // Public

    fun setChosenDocument(documentType: String) {
        val document = model.userDocument(documentType)!!
        documentList = listOf(DocumentItem(document))
        missedDocuments = false
        setupNextButton()
        setupAdapterList()
    }

    // Private

    private fun setupUI() {
        lifecycle.addObserver(vendorsModel)
        url = intent.extras.getString("path", "")

        val urlToParse = url.split("/")

        if (urlToParse.size < 3) {
            showErrorPopup()
        } else {
            val urlNew = urlToParse[0] + "//" + urlToParse[1] + urlToParse[2]
            url = urlNew

            selectCountryFragment = SelectCountryFragment.getInstance()
            selectCountryFragment.setCallback(object : DocumentCallback {
                override fun callback(bundle: Bundle) {
                    chosenCountry = bundle.getString(AppConstants.COUNTRY.key)
                    showSelectDocumentFragment(chosenCountry = chosenCountry)
                }
            })

            vendorsModel.rpDocumentsRequest(url)// Request for RP documentsLive.
            vendorsModel.rpDetailsRequest(url)

            fillSubtitleBold()
            setupAdapter()
            setupAdapterList()
            setupAdapterListener()

            fetchVendorDocs()
            fetchCompanyDetails()
            requestStatusMonitoring()

            setupNextButton()
            cancelTv.setOnClickListener { finish() }
        }

    }

    private fun fillSubtitleBold() {
        val spanText = getString(R.string.identity_verification_by_veriff)
        val words = spanText.split(" ")
        val spanStart = words[0].length + words[1].length + words[2].length + 3
        val spannableBuilder = SpannableStringBuilder(spanText)
        val boldStyle = StyleSpan(Typeface.BOLD)
        spannableBuilder.setSpan(boldStyle, spanStart, spanText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        subtitle1Tv.text = spannableBuilder
    }

    private fun setupAdapter() {
        adapter = RPAdapter()
        contactRecycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        contactRecycler.adapter = adapter
    }

    private fun setupAdapterList() {
        adapter!!.setContacts(listOf(nameItem) + contactList + documentList)
    }

    private fun setupAdapterListener() {
        adapter!!.setOnStageItemClick(object : OnDocumentItemClick {
            override fun onItemClick(view: View, position: Int, type: String) {

                when (position) {
                    0 -> if (model.user().firstName == "") PresentationManager.name(this@AccountActivity)
                    1 -> PresentationManager.phoneNumber(this@AccountActivity)
                    //2 -> PresentationManager.email(this@AccountActivity)
                    2 -> {
//                        selectCountryFragment.show(supportFragmentManager, SelectCountryFragment.FRAGMENT_KEY)
                        showSelectValidDocumentFragment()
                    }
                }
            }
        })
    }

    private fun showSelectValidDocumentFragment() {
        selectAccountDocumentFragment_ = SelectDocumentValidFragment.getInstance()//(bundle)
        selectAccountDocumentFragment_.show(supportFragmentManager, "aaa")
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
            }
        })
        selectAccountDocumentFragment.show(supportFragmentManager, SelectDocumentFragment.FRAGMENT_KEY)
    }

    private fun fetchVendorDocs() {
        vendorsModel.vendorsDocumentsLive().observe(this, Observer<List<VendorDocument>> { it ->
            vendorsDocs = it!!.map { it.type to it }.toMap().toMutableMap()
            vendorDocumentsList = it
            setupName()
            setupDocuments()
            setupContacts()
            setupAdapterList()
            // Handler().postDelayed({ missingInfo(missedName || missedContacts || missedDocuments) }, 200)
        })
    }

    /*
    * Fill text fields information
    * */

    // TODO Add a verification that such a КЗ already exists
    private fun fetchCompanyDetails() {
        vendorsModel.rpDetailsLive().observe(this, Observer<Company> { company ->
            currentCompany = company
            currentCompany?.let {

                titleTv.text = it.name

                GlideApp.with(this)
                        .`as`(PictureDrawable::class.java)
                        //.placeholder(R.drawable.image_loading)
                        //.error(R.drawable.image_error)
                        .transition(withCrossFade())
                        .listener(SvgSoftwareLayerSetter())
                        .load(it.logo).into(rpLogoIv)

                val companyIds = companyModel.companyIds()

                val d = companyModel.companyDocumentDetails(currentCompany!!.id)

                if (company!!.id in companyIds && d != null) {
                    finish()
                    PresentationManager.companyDetails(this, company.id)
                }
            }
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
            val userDocumentsMap = userDocumentsList?.map { it.type to it }?.toMap().orEmpty()
            val userDocumentsPresentTypes = userDocumentsList?.map { it.type }!!.toList()
            val sortedDocTypes = AppDoc.values().map { it.type }.toList()
            val vendorTypes = vendorDocumentsList.map { it.type }
            val vendorTypeCountries = vendorDocumentsList.map { it.type to it.countries }.toMap()

            documentList = emptyList()

            sortedDocTypes.forEach {
                if (it in userDocumentsPresentTypes) {
                    if (userDocumentsMap[it]!!.type in vendorTypes) {
                        if (model.userDocument(it)!!.countryIso.toUpperCase() in vendorTypeCountries[it]!!) {
                            if (documentList.isEmpty()) {
                                documentList = listOf(DocumentItem(userDocumentsMap[it]!!))
                                missedDocuments = false
                                setupAdapterList()
                                setupNextButton()

                            }
                        }
                    }
                }
            }

//            if (documentList.size == 0) {
//                mutableListOf<DocumentItem>(DocumentItem(Document(type = "addDocument")))
//            }
//            vendorsDocs.remove("PASSPORT")
//            vendorsDocs.remove("ID_CARD")
//            vendorsDocs.remove("DRIVERS_LICENSE")
//            vendorsDocs.remove("RESIDENCE_PERMIT_CARD")
//            var count = 0
//             Добавить проверку по странам???
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
            setupNextButton()

            if (documentList.size == 0) {
                documentList = listOf(DocumentItem(Document(type = "addDocument")))
                setupAdapterList()
                setupNextButton()
            }
        })
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
                            onSuccess = {
                                currentCompany!!.status = Status.UNVERIFIED.state
                                currentCompany!!.url = url
                                companyModel.saveCompany(currentCompany!!)
                                Prefs.needCompanySyncCount = Prefs.needCompanySyncCount + 1
                                hideProgress()
                                successful()
                            },
                            onError = {
                                hideProgress(); errorShowPopupImmersive(getString(R.string.error), getString(R.string.documents_sending_error))
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

    private fun errorShowPopupImmersive(title: String, message: String) {
        val builder = getImmersivePopupBuilder()
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog?.dismiss(); finish() }.setCancelable(true)
                .setOnDismissListener { finish() }
        val dialog = builder.create()
        dialog.show()
        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
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

    private fun requestStatusMonitoring() {
        vendorsModel.commonRequestStatus().observe(this, Observer {
            showErrorPopup()
        })
    }

    private fun showErrorPopup() {
        val builder = getImmersivePopupBuilder()
        builder
                .setTitle("Error")
                .setMessage("Relying Party is not available")
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog?.dismiss(); finish() }
                .setOnDismissListener { finish() }
        val dialog = builder.create()
        dialog.show()
        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
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