package com.kimlic.phrase

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_phrase_generate.*


class PhraseGenerateActivity : BaseActivity() {

    // Variables

    private var phraseList: List<String> = emptyList()

    // Mocks

    private val phraseMockList = listOf<String>("fdvdf", "svcsdvc", "sdcsdcs", "scsdcsdc", "tyjfm", "dndy", "dndyn", "vfvfv", "fvfvf", "ddfdfd", "erer", "ererer")

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phrase_generate)

        setupUI()
    }

    // Private

    private fun setupUI() {
        phraseBt.tag = "import"

        phraseBt.setOnClickListener{
            when(it.tag){
                "import" -> {
                    // Import phrases
                    // Use moks
                    phraseList = phraseMockList
                    setPhrases(phraseList)
                    phraseBt.tag = "copy"
                    phraseBt.text = getString(R.string.copy_to_buffer)
                    showPopup(message = getString(R.string.please_write_down_these_words_and_store_them_in_a_safe_place_as_they_can_t_be_recovered))
                }
                "copy" -> {
                    copyToBuffer(phraseList)
                    phraseBt.tag = "save"
                    phraseBt.text = getString(R.string.i_save_the_passphrase)
                }
                "save" ->{
                    PresentationManager.phraseVerify(this)
                }
            }
        }
    }

    private fun showPopup(title: String = "", message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }
                }).setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    private fun setPhrases(list: List<String>) {
        val adapter = PhraseAdapter(this, R.layout.item_phrase, list)
        listView.adapter = adapter

    }

    private fun copyToBuffer(listToSave: List<String>) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val string = StringBuilder()

        if (listToSave.isNotEmpty()) {
            listToSave.forEachIndexed { index, it -> string.append(it + " ") }
            val clipData = ClipData.newPlainText("copyedText", string)
            clipBoard.primaryClip = clipData
            showToast(getString(R.string.phrases_copied_to_buffer))
        }
    }
}
