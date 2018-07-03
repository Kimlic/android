package com.kimlic.address

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_address.*

class AddressActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, KeyEvent.Callback {

    // Constants

    private val PICK_FILE_REQUEST_CODE = 105

    // Variables

    private lateinit var placeAutocompleteAdapter: PlaceAutocompleteAdapter
    private var mGoogleApiClient: GoogleApiClient? = null
    private val LAT_LNG_BOUNDS = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))
    private var isSearchActive = false

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // TODO use file uri
        val uri = data?.data
    }

    override fun onBackPressed() {
        if (isSearchActive) {
            moveDown()
        } else
            super.onBackPressed()
    }
    // Private

    private fun setupUI() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        placeAutocompleteAdapter = PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, citieFilter())

        addressEt.setAdapter(placeAutocompleteAdapter)
        addressEt.setDropDownBackgroundResource(R.color.transparent)

        addressEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    moveDown(); return true
                }
                return false
            }
        })

        addressEt.setOnClickListener { moveUp() }
        addressEt.setOnItemClickListener { parent, view, position, id -> moveDown() }
        addressEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                moveUp()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Button listners
        cancelAddressBt.setOnClickListener { addressEt.text = Editable.Factory().newEditable(""); moveDown() }
        uploadLl.setOnClickListener { pickFile() }
        saveBt.setOnClickListener { manageInput() }
        cancelTv.setOnClickListener { finish() }
        cancelAddressBt.visibility = View.INVISIBLE
    }


    private fun manageInput() {
        // TODO chek if fields are empty; use file address
        Prefs.userAddress = addressEt.text.toString()
        successfull()
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*")
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun moveDown() {
        hideKeyboard()
        logoIv.visibility = View.VISIBLE
        titleTv.visibility = View.VISIBLE
        addressTv.visibility = View.VISIBLE
        spacer.visibility = View.VISIBLE
        documentFl.visibility = View.VISIBLE
        cancelAddressBt.visibility = View.INVISIBLE
        textInputLayout.setPaddingRelative(0, 0, 0, 0)
        rootMiddle.setPaddingRelative(0, 0, 0, 0)
        addressEt.isCursorVisible = false
        addressEt.dismissDropDown()
        addressEt.setSelection(0)
        isSearchActive = false
    }

    private fun moveUp() {
        logoIv.visibility = View.GONE
        titleTv.visibility = View.GONE
        addressTv.visibility = View.GONE
        spacer.visibility = View.GONE
        documentFl.visibility = View.GONE
        cancelAddressBt.visibility = View.VISIBLE
        textInputLayout.setPaddingRelative(0, 0, 24, 0)
        rootMiddle.setPaddingRelative(0, 48, 0, 0)
        addressEt.isCursorVisible = true
        addressEt.setSelection(addressEt.text.length)
        isSearchActive = true
    }

    private fun citieFilter() = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build()

    private fun successfull() {
        val fragment = AddressSuccesfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@AddressActivity)
            }
        })
        fragment.show(supportFragmentManager, AddressSuccesfullFragment.FRAGMENT_KEY)
    }

    // Implementation

    override fun onConnectionFailed(p0: ConnectionResult) {}
}
