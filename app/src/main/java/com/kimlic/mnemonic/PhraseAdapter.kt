package com.kimlic.mnemonic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kimlic.R

class PhraseAdapter(private var phraseContext: Context, var resource: Int, var list: List<String>) : ArrayAdapter<String>(phraseContext, resource, list) {

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(phraseContext).inflate(R.layout.item_phrase, parent, false)

        val phrasePosition = view.findViewById<TextView>(R.id.positionTv)
        val phrase = view.findViewById<TextView>(R.id.phrase)

        phrasePosition.text = String.format("%d ", position + 1)
        phrase.text = list[position]

        return view
    }
}