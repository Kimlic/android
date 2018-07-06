package com.kimlic.camera_id_verification.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.camera.CameraBaseFragment
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_portrait_photo.*

class PortraitPhotoFragment : CameraBaseFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): PortraitPhotoFragment {
            val fragment = PortraitPhotoFragment()
            bundle.putInt(AppConstants.cameraType.key, AppConstants.cameraFront.intKey)
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_portrait_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    // Private

    private fun setupUI() {
        backBt.setOnClickListener { activity!!.finish() }
    }


}