package com.kimlic.name

import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

import com.kimlic.BaseActivity
import com.kimlic.R

import kotlinx.android.synthetic.main.activity_name.*


class NameActivity : BaseActivity() {

    // Binding

    @BindView(R.id.nameEt)
    lateinit var firstNameField: EditText
    @BindView(R.id.lastNameEt)
    lateinit var lastNameField: EditText


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

    // Private

    private fun setupUI() {
        saveBt.setOnClickListener {
            manageInput()
        }
        nameEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (event!!.keyCode == KeyEvent.KEYCODE_ENTER) {
                    manageInput(); hideKeyboard(); return true
                }
                return false
            }
        })

        cancelTv.setOnClickListener { finish() }
        firstNameField.filters = arrayOf(filter(), lengthFilter(20))
        lastNameField.filters = arrayOf(filter(), lengthFilter(20))
    }

    // Filters

    private fun manageInput() {
        if (validFields())
        // Process fielsd
            finish()
    }

    private fun validFields(): Boolean {
        var noError = true

        if (firstNameField.text.length < 3) {
            firstNameField.setError("error")
            noError = false
        } else {
            firstNameField.setError(null)
            noError = true
        }

        if (lastNameField.text.length < 3) {
            lastNameField.setError("error")
            noError = false
        } else {
            lastNameField.setError(null)
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
