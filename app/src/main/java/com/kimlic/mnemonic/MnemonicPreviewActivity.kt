package com.kimlic.mnemonic

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_mnemonic_preview.*

class MnemonicPreviewActivity : BaseActivity() {

    // Variables

    private var phraseList: List<String> = emptyList()

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnemonic_preview)

        setupUI()
    }

    // Private

    private fun setupUI() {
        phraseBt.tag = "show"

        phraseBt.setOnClickListener {
            when (it.tag) {
                "show" -> {
                    // Import phrases
                    // Use moks
                    phraseList = mnemonicList()
                    setPhrases(phraseList)
                    phraseBt.tag = "copy"
                    phraseBt.text = getString(R.string.copy_to_buffer)
                    showPopup(message = getString(R.string.write_down_these_words_stroe_them_in_a_safe_place))
                }
                "copy" -> {
                    copyToBuffer(phraseList)
                    phraseBt.tag = "save"
                    phraseBt.text = getString(R.string.ok_i_save_the_passphrase)
                }
                "save" -> {
                    PresentationManager.phraseVerify(this)
                }
            }
        }
        cancelTv.setOnClickListener { finish() }
    }

    private fun setPhrases(list: List<String>) {
        val adapter = PhraseAdapter(this, R.layout.item_phrase, list)
        listView.adapter = adapter
    }

    private fun mnemonicList(): List<String>{
        val phraseString = KimlicDB.getInstance()!!.userDao().findById(Prefs.userId).mnemonic
        val phraseList = phraseString.split(" ")
        Log.d("TAG", "phrases:"+ phraseList.toString())
        return phraseList

    }

    private fun copyToBuffer(listToSave: List<String>) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val string = StringBuilder()

        if (listToSave.isNotEmpty()) {
            listToSave.forEachIndexed { index, it -> string.append(it + " ") }
            val clipData = ClipData.newPlainText("copyedText", string)
            clipBoard.primaryClip = clipData
        }
    }
}
