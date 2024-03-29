package com.kimlic.mnemonic

import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import kotlinx.android.synthetic.main.activity_mnemonic_preview.*

class MnemonicPreviewActivity : BaseActivity() {

    // Variables

    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnemonic_preview)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupUI()
    }

    // Private

    private fun setupUI() {
        phraseBt.tag = "show"
        phraseBt.setOnClickListener {
            when (it.tag) {
                "show" -> {
                    setPhrases(mnemonicList())
                    phraseBt.tag = "copy"
                    phraseBt.text = getString(R.string.copy_to_buffer)
                    showPopup(message = getString(R.string.write_down_these_words_stroe_them_in_a_safe_place))
                }
                "copy" -> {
                    copyToBuffer(mnemonicList())
                    phraseBt.tag = "save"
                    phraseBt.text = getString(R.string.ok_i_save_the_passphrase)
                }
                "save" -> {
                    PresentationManager.phraseVerify(this)
                }
            }
        }
        changeTv.setOnClickListener { finish() }
    }

    private fun setPhrases(list: List<String>) {
        listView.adapter = PhraseAdapter(this, R.layout.item_phrase, list)
    }

    private fun mnemonicList() = model.user().mnemonic.split(" ")

    private fun copyToBuffer(listToSave: List<String>) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val string = StringBuilder()

        if (listToSave.isNotEmpty()) {
            listToSave.forEachIndexed { _, it -> string.append("$it ") }
            val clipData = ClipData.newPlainText("copied text", string)
            clipBoard.primaryClip = clipData
        }
    }
}