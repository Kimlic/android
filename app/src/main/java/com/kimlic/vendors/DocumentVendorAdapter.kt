package com.kimlic.vendors

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.db.entity.Document
import com.kimlic.stage.adapter.Icons_
import com.kimlic.stage.adapter.OnStageItemClick
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.item_stage.view.*

open class DocumentVendorAdapter : RecyclerView.Adapter<DocumentVendorAdapter.DocumentHolder>() {

    // Variables

    private var documentList: List<Document> = emptyList()
    private lateinit var onStageItemClick: OnStageItemClick

    // Life

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stage, parent, false)
        return DocumentHolder(view)
    }

    override fun getItemCount() = documentList.size

    override fun onBindViewHolder(holder: DocumentHolder, position: Int) {
        holder.bind(documentList.elementAt(position))
        holder.setOnStateItemClick(onStageItemClick)
    }

    // Public

    open fun setDocumentsList(documents: List<Document>) {
        val tempList = mutableListOf<Document>()
        documents.forEach {
            if (it.type == AppDoc.PASSPORT.type) tempList.add(it)
            if (it.type == AppDoc.ID_CARD.type) tempList.add(it)
            if (it.type == AppDoc.DRIVERS_LICENSE.type) tempList.add(it)
            if (it.type == AppDoc.RESIDENCE_PERMIT_CARD.type) tempList.add(it)
        }

        this.documentList = tempList
        notifyDataSetChanged()
    }

    fun setOnStageItemClick(onStageItemClick: OnStageItemClick) {
        this.onStageItemClick = onStageItemClick
    }

    // View Holder

    class DocumentHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Variables

        private lateinit var onStageItemClick: OnStageItemClick
        private lateinit var documentType: String
        private lateinit var documentState: String

        // Public

        fun bind(itemDocument: Document) {
            documentType = itemDocument.type
            documentState = itemDocument.state
            itemView.setOnClickListener(this)

            with(itemView) {
                setOnClickListener(this@DocumentHolder)
                iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                contentTv.setTextColor(resources.getColor(android.R.color.white, null))
                arrowIv.background = resources.getDrawable(Icons_.ARROW_WHITE.icon, null)

                when (itemDocument.type) {
                    AppDoc.PASSPORT.type -> contentTv.text = context.getString(R.string.passport)
                    AppDoc.ID_CARD.type -> contentTv.text = context.getString(R.string.id_card)
                    AppDoc.DRIVERS_LICENSE.type -> contentTv.text = context.getString(R.string.drivers_license)
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> contentTv.text = context.getString(R.string.residence_permit)


//                    "add" -> {
//                        contentTv.text = context.getString(R.string.add_new_document)
//                        contentTv.setTextColor(resources.getColor(R.color.lightBlue, null))
//                        arrowIv.background = resources.getDrawable(Icons_.ARROW_BLUE.icon, null)
//                        iconIv.background = resources.getDrawable(Icons_.ID_BLUE.icon, null)
//                    }
                }
            }
        }

        fun setOnStateItemClick(onStageItemClick: OnStageItemClick) {
            this.onStageItemClick = onStageItemClick
        }

        // Implementation

        override fun onClick(v: View?) {
            onStageItemClick.onClick(view = itemView, position = adapterPosition, type = documentType, state = documentState)
        }
    }
}