package com.kimlic.address

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kimlic.BaseActivity
import com.kimlic.R
import kotlinx.android.synthetic.main.activity_address.*

class AddressActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {

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

    // Private

    private fun setupUI(){
        submitBt.setOnClickListener{showToast("Submit button is pressed")}
        cancelTv.setOnClickListener{showToast("Cancel textView is pressed")}

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        placeAutocompleteAdapter = PlaceAutocompleteAdapter(this,mGoogleApiClient, LAT_LNG_BOUNDS, citieFilter() )
        inputSearchTV.setAdapter(placeAutocompleteAdapter)
    }

    private fun citieFilter() = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build()

    // Implementation

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}
