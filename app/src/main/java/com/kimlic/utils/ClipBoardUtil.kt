package com.kimlic.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipBoardUtil {

    companion object {
        fun copyToClipBoard(context: Context, listToSave: List<String>) {
            val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val string = StringBuilder()

            if (listToSave.isNotEmpty()) {
                listToSave.forEachIndexed { _, it -> string.append("$it ") }
                val clipData = ClipData.newPlainText("copied text", string)
                clipBoard.primaryClip = clipData
            }
        }
    }
}