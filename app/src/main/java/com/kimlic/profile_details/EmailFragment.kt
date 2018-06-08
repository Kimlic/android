package com.kimlic.profile_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import com.kimlic.BaseFragment
import com.kimlic.R
import kotlinx.android.synthetic.main.fragment_email.*

class EmailFragment : BaseFragment() {

    // Companion

    companion object {
        val FRAGMENT_KEY = this::class.java.simpleName

        fun newInstance(bundle: Bundle = Bundle()): EmailFragment {
            val fragment = EmailFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    // Life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(emailEt)
    }

    // Actions

    @OnClick(R.id.nextBt)
    fun onNextClick() {
        if (isEmailValid()) {
            emailEt.setError(null)
            showToast("email is valid continue")
        } else {
            emailEt.setError("invalid")
            showToast("email not valid")
        }
    }

    @OnClick(R.id.backTv)
    fun onCancelCkick() {
        showToast("onCancelClick")
    }

    // Private

    private fun setupUI() {
    }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()
}