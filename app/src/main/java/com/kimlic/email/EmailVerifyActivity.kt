package com.kimlic.email

import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.phone.PhoneSuccessfullFragment
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_email_verify.*

class EmailVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var digitsList: List<@JvmSuppressWildcards EditText>

    // Variables

    private var currentHolder = 0

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

        ButterKnife.bind(this)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(digit1Et)
    }

    // Private

    private fun setupUI() {
        digitsList[currentHolder].requestFocus()
        verifyBt.setOnClickListener { managePins() }

        cancelTv.setOnClickListener { finish(); showToast("change Email") }
        setupDigitListner()

        digitsList[3].setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (event!!.keyCode == KeyEvent.KEYCODE_ENTER) {
                    managePins(); hideKeyboard(); return true
                }
                return false
            }
        })
    }

    private fun managePins() {
        if (pinEntered()) successfull() else showToast("Pins are NOT entered")
    }

    private fun pinEntered(): Boolean {
        var count = 0
        digitsList.forEach { it -> if (!it.text.isEmpty()) count++ }

        return (count == 4)
    }

    private fun successfull() {
        val fragment = EmailSuccesfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@EmailVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, PhoneSuccessfullFragment.FRAGMENT_KEY)
    }

    private fun setupDigitListner() {
        digitsList.forEach {

            it.setOnKeyListener(object : View.OnKeyListener {
                val position = Integer.valueOf(it.tag.toString())

                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event!!.getAction() != KeyEvent.ACTION_DOWN)
                        return false

                    when (keyCode) {
                        KeyEvent.KEYCODE_0 -> moveNext(0)
                        KeyEvent.KEYCODE_1 -> moveNext(1)
                        KeyEvent.KEYCODE_2 -> moveNext(2)
                        KeyEvent.KEYCODE_3 -> moveNext(3)
                        KeyEvent.KEYCODE_4 -> moveNext(4)
                        KeyEvent.KEYCODE_5 -> moveNext(5)
                        KeyEvent.KEYCODE_6 -> moveNext(6)
                        KeyEvent.KEYCODE_7 -> moveNext(7)
                        KeyEvent.KEYCODE_8 -> moveNext(8)
                        KeyEvent.KEYCODE_9 -> moveNext(9)
                        KeyEvent.KEYCODE_DEL -> moveBack()
                    }
                    return false
                }

                private fun moveNext(keyCode: Int) {
                    if (position < 4) {
                        digitsList.elementAt(position).text = Editable.Factory.getInstance().newEditable(keyCode.toString())
                        digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_dark)
                        if (position < 3) digitsList.elementAt(position + 1).requestFocus()
                    }
                }

                private fun moveBack() {
                    digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_trasparent)
                    digitsList.elementAt(position).text = Editable.Factory.getInstance().newEditable("")

                    if (position > 0) digitsList.elementAt(position - 1).requestFocus()
                }
            })
        }
    }
}