package com.kimlic.documents.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kimlic.R
import com.kimlic.documents.DocIcons
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.item_document.view.*

class DocumentAdapter : BaseAdapter() {

    // Variables

    private var documents: List<String> = emptyList()
    var selectedPosition: Int = -1
    private lateinit var context: Context

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        context = parent!!.context
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_document, parent, false)
        val checked = position == selectedPosition

        with(view) {
            choisIv.visibility = View.INVISIBLE
            documentTv.setTextColor(context.getColor(R.color.lightBlue))

            when (documents[position]) {
                AppDoc.PASSPORT.type -> {
                    documentTv.text = context.resources.getString(R.string.passport); tag = AppDoc.PASSPORT.type
                    documentIv.background = if (!checked) icon(DocIcons.PASSPORT_BLUE.icon) else icon(DocIcons.PASSPORT_WHITE.icon)
                }
                AppDoc.ID_CARD.type -> {
                    documentTv.text = context.resources.getString(R.string.id_card); tag = AppDoc.ID_CARD.type
                    documentIv.background = if (!checked) icon(DocIcons.ID_BLUE.icon) else icon(DocIcons.ID_WHITE.icon)
                }
                AppDoc.DRIVERS_LICENSE.type -> {
                    documentTv.text = context.resources.getString(R.string.drivers_license); tag = AppDoc.DRIVERS_LICENSE
                    documentIv.background = if (!checked) icon(DocIcons.DRIVERS_LICENSE_BLUE.icon) else icon(DocIcons.DRIVERS_LICENSE_WHITE.icon)
                }
                AppDoc.RESIDENCE_PERMIT_CARD.type -> {
                    documentTv.text = context.getString(R.string.residence_permit); tag = AppDoc.RESIDENCE_PERMIT_CARD.type
                    documentIv.background = if (!checked) icon(DocIcons.RESIDENCE_PERMIT_BLUE.icon) else icon(DocIcons.RESIDENCE_PERMIT_WHITE.icon)
                }
                AppDoc.SOCIAL_SECURITY_CARD.type -> {
                    documentTv.text = context.getString(R.string.social_security_card); tag = AppDoc.SOCIAL_SECURITY_CARD
                    documentIv.background = if (!checked) icon(DocIcons.SOCIAL_SECURITY_CARD_BLUE.icon) else icon(DocIcons.SOCIAL_SECURITY_CARD_WHITE.icon)
                }
                AppDoc.BIRTH_CERTIFICATE.type -> {
                    documentTv.text = context.getString(R.string.birth_certificate); tag = AppDoc.BIRTH_CERTIFICATE
                    documentIv.background = if (!checked) icon(DocIcons.BIRTH_CERTIFICATE_BLUE.icon) else icon(DocIcons.BIRTH_CERTIFICATE_WHITE.icon)
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

    fun setDocuments(documents: List<String>?) {
        this.documents = documents!!
        notifyDataSetChanged()
    }

    // Private

    private fun icon(icon: Int) = context.resources.getDrawable(icon, null)
}