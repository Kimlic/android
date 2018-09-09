package com.kimlic.documents.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kimlic.R
import com.kimlic.documents.IconsDocument
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.item_document.view.*

class DocumentAdapter : BaseAdapter() {

    // Variables

    private var documents: List<String> = emptyList()
    var selectedPosition: Int = -1

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent!!.context
        val view: View = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)

        view.choisIv.visibility = View.INVISIBLE
        view.documentTv.setTextColor(context.getColor(R.color.lightBlue))

        when (documents[position]) {
            AppDoc.PASSPORT.type -> {
                view.documentTv.text = context.resources.getString(R.string.passport); view.tag = AppDoc.PASSPORT.type
                view.documentIv.background = context.resources.getDrawable(IconsDocument.BIRTH_CERTIFICATE_BLUE.icon, null)
            }
            AppDoc.ID_CARD.type -> {
                view.documentTv.text = context.resources.getString(R.string.id_card); view.tag = AppDoc.ID_CARD.type
                view.documentIv.background = context.resources.getDrawable(IconsDocument.ID_BLUE.icon, null)
            }
            AppDoc.DRIVERS_LICENSE.type -> {
                view.documentTv.text = context.resources.getString(R.string.drivers_license); view.tag = AppDoc.DRIVERS_LICENSE
                view.documentIv.background = context.resources.getDrawable(IconsDocument.DRIVERS_LICENSE_BLUE.icon, null)
            }
            AppDoc.RESIDENCE_PERMIT_CARD.type -> {
                view.documentTv.text = context.getString(R.string.residence_permit); view.tag = AppDoc.RESIDENCE_PERMIT_CARD.type
                view.documentIv.background = context.resources.getDrawable(IconsDocument.RESIDENCE_PERMIT_BLUE.icon, null)
            }
            AppDoc.SOCIAL_SECURITY_CARD.type -> {
                view.documentTv.text = context.getString(R.string.social_security_card); view.tag = AppDoc.SOCIAL_SECURITY_CARD
                view.documentIv.background = context.resources.getDrawable(IconsDocument.SOCIAL_SECURITY_CARD_BLUE.icon, null)
            }
            AppDoc.BIRTH_CERTIFICATE.type -> {
                view.documentTv.text = context.getString(R.string.birth_certificate); view.tag = AppDoc.BIRTH_CERTIFICATE
                view.documentIv.background = context.resources.getDrawable(IconsDocument.DRIVERS_LICENSE_BLUE.icon, null)
            }
        }

        if (position == selectedPosition) {
            view.choisIv.visibility = View.VISIBLE
            view.documentTv.setTextColor(Color.WHITE)
        }

        return view
    }

    override fun getItem(position: Int): Any = documents[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = documents.size

    // Public

    fun setDocuments(documents: List<String>?) {
        this.documents = documents!!
        notifyDataSetChanged()
    }
}