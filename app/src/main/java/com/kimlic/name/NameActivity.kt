package com.kimlic.name

import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.preferences.Prefs
import com.kimlic.utils.BaseCallback

import kotlinx.android.synthetic.main.activity_name.*


class NameActivity : BaseActivity(), TextView.OnEditorActionListener {

    // Binding

    @BindView(R.id.nameEt)
    lateinit var name: EditText
    @BindView(R.id.lastNameEt)
    lateinit var lastName: EditText

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

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
        saveBt.setOnClickListener {
            manageInput()
        }
        nameEt.setOnEditorActionListener(this)
        lastNameEt.setOnEditorActionListener(this)

        cancelTv.setOnClickListener { finish() }
        name.filters = arrayOf(lengthFilter(20))//,filter())
        lastName.filters = arrayOf(lengthFilter(20))//,filter())
    }

    // Filters

    private fun manageInput() {
        if (validFields()) {
            // Process fielsd
            Prefs.userName = name.text.toString()
            Prefs.userLastName = lastName.text.toString()
            succesfull()
        }
    }

    private fun succesfull() {
        val fragment = NameSuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                finish()
            }
        })
        fragment.show(supportFragmentManager, NameSuccessfullFragment.FRAGMENT_KEY.name)
    }

    private fun validFields(): Boolean {
        var noError = true

        if (name.text.length < 3) {
            name.setError("error")
            noError = false
        } else {
            name.setError(null)
            noError = true
        }

        if (lastName.text.length < 3) {
            lastName.setError("error")
            noError = false
        } else {
            lastName.setError(null)
            noError = true
        }

        return noError
    }

    private fun filter(): InputFilter {
        val filter = InputFilter { src, start, end, dst, dstart, dend ->
            if (src == "") return@InputFilter src
            if (Character.isLetter(src.last()) && !Character.isWhitespace(src.last())) return@InputFilter src else ""
        }
        return filter
    }

    private fun lengthFilter(length: Int): InputFilter.LengthFilter = InputFilter.LengthFilter(length)
}
