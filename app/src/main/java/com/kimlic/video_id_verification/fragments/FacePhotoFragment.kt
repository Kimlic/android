package com.kimlic.video_id_verification.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.camera.CameraBaseFragment
import com.kimlic.video_id_verification.DocumentVerifyViewModel

class FacePhotoFragment : CameraBaseFragment() {


    // Variables

    private lateinit var viewModel: DocumentVerifyViewModel

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_face_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DocumentVerifyViewModel::class.java)
        viewModel.documentType.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                Log.d("TAGLIVEDATA", t)
            }
        })

        val docType = viewModel.documentType
        docType.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                Log.d("TAGLIVEDATA", t)
            }
        })
    }

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): FacePhotoFragment {
            val fragment = FacePhotoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}