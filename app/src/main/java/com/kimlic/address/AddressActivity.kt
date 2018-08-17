package com.kimlic.address

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.constraint.ConstraintLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
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
import com.kimlic.documents.BillActivity
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_address.*
import java.io.File

class AddressActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, KeyEvent.Callback {

    // Constants

    private val PICK_FILE_REQUEST_CODE = 105
    private val TAKE_PHOTO_REQUEST_CODE = 106

    // Variables

    private lateinit var placeAutocompleteAdapter: PlaceAutocompleteAdapter
    private lateinit var addressBitmap: Bitmap
    private lateinit var addressData: ByteArray
    private var addressId: Long = 0
    private var googleApiClient: GoogleApiClient? = null
    private val LAT_LNG_BOUNDS = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))
    private var isSearchActive = false
    private var isPhotoPresent: Boolean = false
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            PICK_FILE_REQUEST_CODE -> {

                val uri = data!!.data
                val uriString = uri.toString()
                val myFile = File(uriString)

//                val path = myFile.absolutePath
                var displayName: String? = null

                if (uriString.startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = this.contentResolver.query(uri, null, null, null, null)
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
                addBt.setOnClickListener({})
            }

            TAKE_PHOTO_REQUEST_CODE -> {
                val data_ = data?.extras?.getByteArray(AppConstants.documentByteArray.key)
                addressBitmap = BitmapFactory.decodeByteArray(data_, 0, data_!!.size)
                addressData = data_

                showPickedBitmap(addressBitmap)
                isPhotoPresent = true
            }
        }
    }

    override fun onBackPressed() {
        if (isSearchActive)
            moveDown()
        else
            super.onBackPressed()
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupAddressSearch()
        saveBt.setOnClickListener { manageInput() }
        addBt.setOnClickListener { pickFile() }
        changeTv.setOnClickListener { model.deleteAddres(addressId = addressId); finish() }
    }

    private fun manageInput() {
        if (fieldsAreValid()) {
            model.addUserAddress(value = addressEt.text.toString(), data = addressData)
            successful()
        }
    }

    private fun fieldsAreValid(): Boolean {
        val noError = if (addressEt.text.length < 3) {
            addressEt.error = getString(R.string.error); false
        } else {
            addressEt.error = null; true
        }

        return (noError && isPhotoPresent)
    }

    private fun setupAddressSearch() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        placeAutocompleteAdapter = PlaceAutocompleteAdapter(this, googleApiClient, LAT_LNG_BOUNDS, cityFilter())

        addressEt.setAdapter(placeAutocompleteAdapter)
        addressEt.setDropDownBackgroundResource(R.color.transparent)

        addressEt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                moveDown(); return@OnEditorActionListener true
            }
            false
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

        cancelAddressBt.setOnClickListener { addressEt.text = Editable.Factory().newEditable(""); moveDown() }
        cancelAddressBt.visibility = View.INVISIBLE
    }

    private fun showPickedBitmap(bitmap: Bitmap) {
        addBt.visibility = View.GONE

        val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(16, 16, 16, 16)
        documentIv.layoutParams = layoutParams
        documentIv.background = null
        documentIv.setImageBitmap(cropped(bitmap))
    }

    private fun pickFile() {
        val intent = Intent(this, BillActivity::class.java)
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE)
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

    private fun cityFilter() = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build()

    private fun successful() {
        val fragment = AddressSuccesfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@AddressActivity)
            }
        })
        fragment.show(supportFragmentManager, AddressSuccesfulFragment.FRAGMENT_KEY)
    }

    // Implementation

    override fun onConnectionFailed(p0: ConnectionResult) {}

    // Private helpers

    private fun cropped(bitmap: Bitmap): Bitmap {
        val originalBitmap = rotateBitmap(bitmap, 90f)
        val width = originalBitmap.width
        val height = originalBitmap.height
        val bitmapCropped = Bitmap.createBitmap(originalBitmap, (0.15 * width).toInt(), (0.08 * height).toInt(), (0.7 * width).toInt(), (0.5 * height).toInt())
        return bitmapCropped
    }
}
