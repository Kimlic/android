package com.kimlic.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.utils.BaseCallback

class TouchIDFragment : BasePopupFragment() {

    private lateinit var callback: BaseCallback

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance() = TouchIDFragment()
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
    }
}