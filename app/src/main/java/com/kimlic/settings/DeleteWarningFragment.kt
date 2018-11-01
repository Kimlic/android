package com.kimlic.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.fragment_delete_warning.*

class DeleteWarningFragment : BasePopupFragment() {

    // Variables

    private lateinit var callback: BaseCallback

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance(bundle: Bundle = Bundle()): DeleteWarningFragment {
            val fragment = DeleteWarningFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_delete_warning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        setupUI()
    }

    // Public

    fun setCallback(callback: BaseCallback) {
        this.callback = callback
    }

    // Private

    private fun setupUI() {
        isCancelable = true
        dismissBt.setOnClickListener { dismiss() }
        cancelBt.setOnClickListener { dismiss() }
        deleteBt.setOnClickListener { callback.callback() }
    }
}