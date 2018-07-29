package com.kimlic.address

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.kimlic.db.entity.Address
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.ProfileViewModel
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_address.*
import java.io.File

class AddressActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, KeyEvent.Callback {

    // Constants

    private val PICK_FILE_REQUEST_CODE = 105

    // Variables

    private lateinit var placeAutocompleteAdapter: PlaceAutocompleteAdapter
    private var mGoogleApiClient: GoogleApiClient? = null
    private val LAT_LNG_BOUNDS = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))
    private var isSearchActive = false
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_FILE_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                val uri = data!!.getData()
                val uriString = uri.toString()
                val myFile = File(uriString)

//                val path = myFile.absolutePath
                var displayName: String? = null

                if (uriString.startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.name
                }

                Log.d("TAG", "display name = " + displayName)
                data.extras.getByteArray("")
                browsBt.setOnClickListener({})

            }
        }
    }

    override fun onBackPressed() {
        if (isSearchActive) {
            moveDown()
        } else
            super.onBackPressed()
    }
    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
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
        cancelTv.setOnClickListener { finish() }
        cancelAddressBt.visibility = View.INVISIBLE
        saveBt.setOnClickListener { manageInput() }
        browsBt.setOnClickListener { pickFile() }
    }

    private fun manageInput() {
        val address = Address(userId = Prefs.currentId, value = addressEt.text.toString())
        model.addUserAddress(Prefs.currentAccountAddress, address)
        successfull()
    }

    private fun pickFile() {
        PresentationManager.verifyBill(this)
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

    private fun citieFilter() = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build()

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
