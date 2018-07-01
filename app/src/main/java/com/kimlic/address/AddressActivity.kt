package com.kimlic.address

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
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
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_address.*

class AddressActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {

    // Constants

    private val PICK_FILE_REQUEST_CODE = 105

    // Variables

    private lateinit var placeAutocompleteAdapter: PlaceAutocompleteAdapter
    private var mGoogleApiClient: GoogleApiClient? = null
    private val LAT_LNG_BOUNDS = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(inputSearchTV)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri = data?.data
    }

    // Private

    private fun setupUI() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        placeAutocompleteAdapter = PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null)//, citieFilter())

        inputSearchTV.setAdapter(placeAutocompleteAdapter)

        cancelTv.setOnClickListener { finish() }
        uploadLl.setOnClickListener { pickFile() }

        inputSearchTV.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    manageInput(); hideKeyboard(); return true
                }
                return false
            }
        })

        saveBt.setOnClickListener { manageInput() }
    }


    private fun manageInput(){
        Prefs.userAddress = inputSearchTV.text.toString()
        finish()
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*")
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun citieFilter() = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build()

    // Implementation

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}
