package com.kimlic.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.touch_id_fragment.*

class TouchIDFragment : BasePopupFragment() {

    private lateinit var callback: BaseCallback

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): TouchIDFragment {
            val fragment = TouchIDFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Live

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.touch_id_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(R.drawable.rounded_background_fragment_tarnsparent)
    }

    // Public

    fun setCallback(callback: BaseCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        isCancelable = false
        val mode = arguments!!.get("flag") as String
        retakelBt.setOnClickListener {
            dismiss()
            when (mode) {
                "unlock" -> {
                    (activity!! as TouchIdActivity).fingerprintService == null; activity!!.finish(); PresentationManager.passcodeUnlock(activity!!)
                }
                "unlock_finish" -> {
                    (activity!! as TouchIdActivity).fingerprintService == null; activity!!.finish(); PresentationManager.passcodeFinish(activity!!)
                }
            }
        }
    }
}