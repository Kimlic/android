package com.kimlic.profile_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import butterknife.OnClick
import com.kimlic.BaseFragment
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_email_verify.*

class EmailVerifyFragment : BaseFragment() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var mDigitListE: List<@JvmSuppressWildcards EditText>

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): EmailVerifyFragment {
            val fragment = EmailVerifyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_email_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        setupUI()
    }

    // Actions

    @OnClick(R.id.verifyBt)
    fun onVerifyEmailClick() {
        if (pinEntered())
            showToast("Pins Are entered")
        else
            showToast("Pins are NOT entered")
    }

    @OnClick(R.id.changeTv)
    fun onCancelClick(){
        showToast("Cancel")
    }

    // Private

    private fun setupUI() {
        showSoftKeyboard(digit1Et)
    }

    private fun pinEntered(): Boolean {
        var count = 0
        mDigitListE.forEach { it -> if (!it.text.isEmpty()) count++ }

        return (count == 4)
    }
}