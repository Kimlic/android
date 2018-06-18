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
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.risks_fragment.*

class RisksFragment : BasePopupFragment() {

    // Binding

    @BindViews(R.id.lowerBt, R.id.upperBt)
    lateinit var buttons: List<@JvmSuppressWildcards Button>
    @BindViews(R.id.lowerView, R.id.upperView)
    lateinit var views: List<@JvmSuppressWildcards LinearLayout>

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): RisksFragment {
            val fragment = RisksFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.risks_fragment, container, false)
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

        val recoveryOffered = arguments?.getBoolean("recovery", true)!!
        val passcodeOffered = arguments?.getBoolean("passcode", true)!!

        var count = 0

        if (!recoveryOffered) {
            views[count].visibility = View.VISIBLE
            buttons[count].text = getString(R.string.enable_account_recovery)
            buttons[count++].setOnClickListener { Prefs.isRecoveryOffered = true; dismiss(); PresentationManager.passhraseCreate(activity!!) }
        }

        if (!passcodeOffered) {
            views[count].visibility = View.VISIBLE
            buttons[count].text = getString(R.string.create_passcode)
            buttons[count++].setOnClickListener { Prefs.isPasscodeOffered = true; dismiss(); PresentationManager.passcode(activity!!) }
        }

        subtitleTv.text = resources.getString(R.string.you_have_main_risks, count)
    }
}