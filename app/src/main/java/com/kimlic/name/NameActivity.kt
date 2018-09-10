package com.kimlic.name

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.model.ProfileViewModel
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : BaseActivity(), TextView.OnEditorActionListener {

    // Variables

    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        ButterKnife.bind(this)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(nameEt)
    }

    // Implementation

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            manageInput(); hideKeyboard(); return true
        }
        return false
    }

    // Private

    private fun setupUI() {
        saveBt.setOnClickListener { manageInput() }
        nameEt.setOnEditorActionListener(this)
        lastNameEt.setOnEditorActionListener(this)

        changeTv.setOnClickListener { finish() }

        nameEt.filters = arrayOf(lengthFilter(20))//,filter())
        lastNameEt.filters = arrayOf(lengthFilter(20))//,filter())
    }

    // Filters

    private fun manageInput() {
        if (validFields()) {
            model.updateUserName(nameEt.text.toString(), lastNameEt.text.toString())
            successful()
        }
    }

    private fun successful() {
        val fragment = NameSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
            }
        })
        fragment.show(supportFragmentManager, NameSuccessfulFragment.FRAGMENT_KEY.name)
    }


    // @formatter:off
    private fun validFields(): Boolean {
        val error = getString(R.string.error)
        val nameError = if (nameEt.text.length < 3) { nameEt.error = error; false } else { nameEt.error = null; true }
        val lastNameError =  if (lastNameEt.text.length < 3) { lastNameEt.error = error; false } else { lastNameEt.error = null; true }
        return (nameError && lastNameError)
    }
    //@formatter:on


    private fun filter(): InputFilter {
        return InputFilter { src, _, _, _, _, _ ->
            if (src == "") return@InputFilter src
            if (Character.isLetter(src.last()) && !Character.isWhitespace(src.last())) return@InputFilter src else ""
        }
    }

    private fun lengthFilter(length: Int): InputFilter.LengthFilter = InputFilter.LengthFilter(length)
}