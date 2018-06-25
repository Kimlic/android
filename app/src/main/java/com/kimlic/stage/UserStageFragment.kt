package com.kimlic.stage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_stage_user.*

class UserStageFragment : BaseFragment() {

    // Variables

    private lateinit var uaserName: String

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): UserStageFragment {
            val fragment = UserStageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stage_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        userPhotoIv.invalidate()
    }

    // Private

    private fun setupUI() {
        setUserPhoto()
        setupListners()
        setupFielsds()
        setupTitles()
        setProgress(35)
    }

    private fun setUserPhoto() {
        if (!Prefs.isUserPhotoTaken) {
            takePhotoLl.visibility = View.INVISIBLE
        }

        (userPhotoIv as UserPhotoView).showUserPhoto(AppConstants.userPortraitFileName.key)

    }

    private fun setProgress(progress: Int = 0) {
        (userPhotoIv as UserPhotoView).setProgress(progress)
    }

    private fun setBlueScreen() {
        (userPhotoIv as UserPhotoView).showBlueScreen()
    }

    private fun setupListners() {
        settingsBt.setOnClickListener { PresentationManager.settings(activity!!) }
        nameItem.setOnClickListener { PresentationManager.name(activity!!) }
        phoneItem.setOnClickListener { showToast("phone item is clicked") }
        emailItem.setOnClickListener { PresentationManager.email(activity!!) }
        idItem.setOnClickListener { PresentationManager.documentChooseVerify(activity!!) }
        addressItem.setOnClickListener { showToast("addres item is clicked"); PresentationManager.address(activity!!) }

        titleTv.setOnClickListener { setProgress(90); setUserPhoto() }
        subtitleTv.setOnClickListener { setProgress(20); setBlueScreen() }


        takePhotoLl.setOnClickListener {
            // Todo creare call to photo
            it.visibility = View.INVISIBLE
            showToast("TakePortrait photo is clicked")
        }
    }

    // Mocks

    private fun setupTitles() {
        if (true) titleTv.text = getString(R.string.add_your_name)
        if (true) subtitleTv.text = getString(R.string.id_assurance)
    }

    private fun setupFielsds() {
        setupNameField()
        setupPhoneField()
        setupEmailField()
        setupIDField()
        setupAddressField()
    }

    // Setup profile Fields

    private fun setupEmailField(email: String = "") {
        emailIv.background = resources.getDrawable(if (email.equals("")) Icons.EMAIL_BLUE.icon else Icons.EMAIL_WHITE.icon, null)
        emailArrow.background = resources.getDrawable(if (email.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        emailTv.text = if (email.equals("")) getString(R.string.add_your_email) else email
    }

    private fun setupNameField(name: String = "") {
        nameIv.background = resources.getDrawable(if (name.equals("")) Icons.NAME_BLUE.icon else Icons.NAME_WHITE.icon, null)
        nameArrow.background = resources.getDrawable(if (name.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        nameTv.text = if (name.equals("")) getString(R.string.add_your_full_name) else name
    }

    private fun setupPhoneField(phone: String = "") {
        phoneIv.background = resources.getDrawable(if (phone.equals("")) Icons.PHONE_BLUE.icon else Icons.PHONE_WHITE.icon, null)
        phoneArrow.background = resources.getDrawable(if (phone.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        phoneTv.text = if (phone.equals("")) getString(R.string.add_your_phone) else phone
    }

    private fun setupIDField(id: String = "") {
        idIv.background = resources.getDrawable(if (id.equals("")) Icons.ID_BLUE.icon else Icons.ID_WHITE.icon, null)
        idArrow.background = resources.getDrawable(if (id.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        idTv.text = if (id.equals("")) getString(R.string.verify_your_id) else id
    }

    private fun setupAddressField(address: String = "") {
        addressIv.background = resources.getDrawable(if (address.equals("")) Icons.LOCATION_BLUE.icon else Icons.LOCATION_WHITE.icon, null)
        addressArrow.background = resources.getDrawable(if (address.equals("")) Icons.ARROW_BLUE.icon else Icons.ARROW_WHITE.icon, null)
        addressTv.text = if (address.equals("")) getString(R.string.add_your_address) else address
    }

}

internal enum class Icons(val icon: Int) {

    NAME_BLUE(R.drawable.ic_profile_name_icon_blue),
    NAME_WHITE(R.drawable.ic_profile_name_icon_white),

    PHONE_BLUE(R.drawable.ic_profile_phone_icon_blue),
    PHONE_WHITE(R.drawable.ic_profile_phone_icon_white),

    EMAIL_BLUE(R.drawable.ic_profile_email_icon_blue),
    EMAIL_WHITE(R.drawable.ic_profile_email_icon_white),

    ID_BLUE(R.drawable.ic_profile_id_icon_blue),
    ID_WHITE(R.drawable.ic_profile_id_icon_white),

    LOCATION_BLUE(R.drawable.ic_profile_blue_location_icon),
    LOCATION_WHITE(R.drawable.ic_profile_white_location_icon),

    ARROW_BLUE(R.drawable.ic_blue_menu_arrow),
    ARROW_WHITE(R.drawable.ic_white_menu_arrow)
}



