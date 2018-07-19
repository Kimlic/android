package com.kimlic.mnemonic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kimlic.R

class PhraseAdapter(var phraseCntext: Context, var resource: Int, var list: List<String>) : ArrayAdapter<String>(phraseCntext, resource, list) {

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View

        if (convertView == null) view = LayoutInflater.from(phraseCntext).inflate(R.layout.item_phrase, parent, false)
        else view = convertView

        val phrasePosition = view.findViewById<TextView>(R.id.positionTv)
        val phrase = view.findViewById<TextView>(R.id.phrase)

        phrasePosition.text = String.format("%d ", position + 1)
        phrase.text = list[position]

        return view
    }
}