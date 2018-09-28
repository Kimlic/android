package com.kimlic.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kimlic.R
import com.kimlic.account.fragment.SelectDocumentValidFragment
import com.kimlic.utils.AppDoc
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_document_small_valid.view.*

class DocumentValidAdapter : BaseAdapter() {

    // Variables

    private var documents: List<SelectDocumentValidFragment.ValidDocument> = emptyList()
    private lateinit var context: Context

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        context = parent!!.context
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_document_small_valid, parent, false)
        val path = "file:///android_asset/flags/${documents[position].document.country}.png"

        with(view) {
            choisIv.visibility = View.INVISIBLE
            documentTv.setTextColor(context.getColor(R.color.lightBlue))

            with(documentTv) {
                when (documents[position].document.type) {
                    AppDoc.PASSPORT.type -> text = context.getString(R.string.passport)
                    AppDoc.ID_CARD.type -> text = context.getString(R.string.id_card)
                    AppDoc.DRIVERS_LICENSE.type -> text = context.getString(R.string.drivers_license)
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> text = context.getString(R.string.residence_permit)
                    AppDoc.SOCIAL_SECURITY_CARD.type -> text = context.getString(R.string.social_security_card)
                    AppDoc.BIRTH_CERTIFICATE.type -> text = context.getString(R.string.birth_certificate)
                }
            }

            if (documents[position].isValid) {
                choisIv.visibility = View.VISIBLE
                documentTv.setTextColor(context.getColor(android.R.color.white))
            }
            choisIv.visibility = View.GONE// TODO remove
            Picasso.get().load(path).noFade().into(flagIv)
        }

        return view
    }

    override fun getItem(position: Int): Any = documents[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = documents.size

    // Public

    fun setDocuments(documents: List<SelectDocumentValidFragment.ValidDocument>?) {
        this.documents = documents!!
        notifyDataSetChanged()
    }
}