package com.kimlic.stage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BasePopupFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.fragment_risks.*

class RisksFragment : BasePopupFragment() {

    // Binding

    @BindViews(R.id.lowerBt, R.id.upperBt)
    lateinit var buttons: List<@JvmSuppressWildcards Button>
    @BindViews(R.id.lowerView, R.id.upperView)
    lateinit var views: List<@JvmSuppressWildcards LinearLayout>

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName!!

        fun newInstance(bundle: Bundle = Bundle()): RisksFragment {
            val fragment = RisksFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        setupUI()
    }

    // private

    private fun setupUI() {
        isCancelable = true
        dismissBt.setOnClickListener { dismiss() }

        val recoveryEnabled = arguments?.getBoolean("recovery", true)!!
        val passcodeEnabled = arguments?.getBoolean("passcode", true)!!
        var count = 0

        if (!recoveryEnabled) {
            views[count].visibility = View.VISIBLE
            buttons[count].text = getString(R.string.enable_account_recovery)
            buttons[count++].setOnClickListener { dismiss(); PresentationManager.passphraseCreate(activity!!) }
        }

        if (!passcodeEnabled) {
            views[count].visibility = View.VISIBLE
            buttons[count].text = getString(R.string.create_passcode)
            buttons[count++].setOnClickListener { dismiss(); PresentationManager.passcode(activity!!, StageActivity.SECURITY_REQUEST_CODE) }
        }

        subtitleTv.text = resources.getString(R.string.you_have_main_risks, count)
    }
}