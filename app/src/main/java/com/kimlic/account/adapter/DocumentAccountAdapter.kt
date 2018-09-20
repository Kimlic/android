package com.kimlic.account.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.documents.DocIcons
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.item_document.view.*

class DocumentAccountAdapter : BaseAdapter() {

    // Variables

    private var documents: List<Document> = emptyList()
    var selectedPosition: Int = -1
    private lateinit var context: Context

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        context = parent!!.context
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_document_small, parent, false)
        val checked = position == selectedPosition

        with(view) {
            choisIv.visibility = View.INVISIBLE
            documentTv.setTextColor(context.getColor(R.color.lightBlue))

            when (documents[position].type) {

                AppDoc.PASSPORT.type -> {
                    if (documents[position].number == "") {
                        documentTv.text = context.getString(R.string.add_passport); tag = AppDoc.PASSPORT.type
                        documentIv.background = if (!checked) icon(DocIcons.PASSPORT_BLUE.icon) else icon(DocIcons.PASSPORT_WHITE.icon)
                    } else {
                        documentTv.text = context.getString(R.string.passport)
                        documentIv.background = if (!checked) icon(DocIcons.PASSPORT_BLUE.icon) else icon(DocIcons.PASSPORT_WHITE.icon)
                    }
                }
                AppDoc.ID_CARD.type -> {
                    if (documents[position].number == "") {
                        documentTv.text = context.getString(R.string.add_id_card); tag = AppDoc.ID_CARD.type
                        documentIv.background = if (!checked) icon(DocIcons.ID_BLUE.icon) else icon(DocIcons.ID_WHITE.icon)
                    } else {
                        documentTv.text = context.getString(R.string.id_card)
                        documentIv.background = if (!checked) icon(DocIcons.ID_BLUE.icon) else icon(DocIcons.ID_WHITE.icon)
                    }
                }
                AppDoc.DRIVERS_LICENSE.type -> {
                    if (documents[position].number == "") {
                        documentTv.text = context.getString(R.string.add_drivers_license); tag = AppDoc.DRIVERS_LICENSE
                        documentIv.background = if (!checked) icon(DocIcons.DRIVERS_LICENSE_BLUE.icon) else icon(DocIcons.DRIVERS_LICENSE_WHITE.icon)
                    } else {
                        documentTv.text = context.getString(R.string.drivers_license)
                        documentIv.background = if (!checked) icon(DocIcons.DRIVERS_LICENSE_BLUE.icon) else icon(DocIcons.DRIVERS_LICENSE_WHITE.icon)
                    }
                }
                AppDoc.RESIDENCE_PERMIT_CARD.type -> {
                    if (documents[position].number == "") {
                        documentTv.text = context.getString(R.string.add_residense_permit); tag = AppDoc.RESIDENCE_PERMIT_CARD.type
                        documentIv.background = if (!checked) icon(DocIcons.RESIDENCE_PERMIT_BLUE.icon) else icon(DocIcons.RESIDENCE_PERMIT_WHITE.icon)
                    } else {
                        documentTv.text = context.getString(R.string.residence_permit)
                        documentIv.background = if (!checked) icon(DocIcons.RESIDENCE_PERMIT_BLUE.icon) else icon(DocIcons.RESIDENCE_PERMIT_WHITE.icon)
                    }
                }
                AppDoc.SOCIAL_SECURITY_CARD.type -> {
                    if (documents[position].number == "") {
                        documentTv.text = context.getString(R.string.add_social_security_card); tag = AppDoc.SOCIAL_SECURITY_CARD
                        documentIv.background = if (!checked) icon(DocIcons.SOCIAL_SECURITY_CARD_BLUE.icon) else icon(DocIcons.SOCIAL_SECURITY_CARD_WHITE.icon)
                    } else {
                        documentTv.text = context.getString(R.string.social_security_card)
                        documentIv.background = if (!checked) icon(DocIcons.SOCIAL_SECURITY_CARD_BLUE.icon) else icon(DocIcons.SOCIAL_SECURITY_CARD_WHITE.icon)
                    }
                }
                AppDoc.BIRTH_CERTIFICATE.type -> {
                    if (documents[position].number == "") {
                        documentTv.text = context.getString(R.string.add_birth_certificate); tag = AppDoc.BIRTH_CERTIFICATE
                        documentIv.background = if (!checked) icon(DocIcons.BIRTH_CERTIFICATE_BLUE.icon) else icon(DocIcons.BIRTH_CERTIFICATE_WHITE.icon)
                    } else {
                        documentTv.text = context.getString(R.string.birth_certificate)
                        documentIv.background = if (!checked) icon(DocIcons.BIRTH_CERTIFICATE_BLUE.icon) else icon(DocIcons.BIRTH_CERTIFICATE_WHITE.icon)
                    }
                }
            }

            if (checked) {
                choisIv.visibility = View.VISIBLE; documentTv.setTextColor(Color.WHITE)
            }
        }

        return view
    }

    override fun getItem(position: Int): Any = documents[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = documents.size

    // Public

    fun setDocuments(documents: List<Document>?) {
        this.documents = documents!!
        notifyDataSetChanged()
    }

    // Private

    private fun icon(icon: Int) = context.resources.getDrawable(icon, null)
}