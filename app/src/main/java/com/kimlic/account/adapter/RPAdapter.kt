package com.kimlic.account.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.account.AccountItem
import com.kimlic.account.OnDocumentItemClick
import com.kimlic.stage.adapter.Icons_
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.item_account_document.view.*

class RPAdapter : RecyclerView.Adapter<RPAdapter.ContactViewHolder>() {

    // Variables

    private var contactList: List<AccountItem> = emptyList()
    private lateinit var onDocumentItemClick: OnDocumentItemClick

    // Live

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account_document, parent, false)
        return ContactViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position], position)
        holder.setOnDocumentItemClick(onDocumentItemClick)
    }

    // Public

    fun setContacts(contacts: List<AccountItem>) {
        this.contactList = contacts
        notifyDataSetChanged()
    }

    fun setOnStageItemClick(onDocumentItemClick: OnDocumentItemClick) {
        this.onDocumentItemClick = onDocumentItemClick
    }

    // ViewHolder class

    class ContactViewHolder(private val contactView: View, val context: Context) : RecyclerView.ViewHolder(contactView), View.OnClickListener {

        // Variables

        private lateinit var onDocumentItemClick: OnDocumentItemClick
        private var position_: Int = 0
        private var type: String = ""

        fun bind(item: AccountItem, position: Int) {
            position_ = position
            type = item.type
            with(contactView) {

                if (item.value != "") {
                    arrowIv.background = resources.getDrawable(R.drawable.ic_done_green_24dp, null)
                    contentTv.setTextColor(Color.WHITE); isClickable = false; isFocusableInTouchMode = false; isFocusable = false

                } else {setOnClickListener(this@ContactViewHolder)}
                when (item.type) {
                    "USER_NAME" -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.NAME_BLUE.icon);contentTv.text = "Add your Name"
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.NAME_WHITE.icon)
                        }
                    }
                    "email" -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.EMAIL_BLUE.icon);contentTv.text = "Add your Email"
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.EMAIL_WHITE.icon)
                        }
                    }
                    "phone" -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.PHONE_BLUE.icon); contentTv.text = "Add your Phone"
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.PHONE_WHITE.icon)
                        }
                    }
                    AppDoc.PASSPORT.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = "Add your Passport"
                        } else {
                            contentTv.text = "Passport"; iconIv.background = icon(Icons_.ID_WHITE.icon)
                        }
                    }
                    AppDoc.ID_CARD.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = "Add your ID card"
                        } else {
                            contentTv.text = "Id card"; iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.DRIVERS_LICENSE.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = "Add your Driver license"
                        } else {
                            contentTv.text = "Drivers License"; iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = "Add your Residence Permit"
                        } else {
                            contentTv.text = "Residence Permit"; iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.SOCIAL_SECURITY_CARD.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = "Add your Social Securirty Card"
                        } else {
                            contentTv.text = "Social Security Card"; iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.BIRTH_CERTIFICATE.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = "Add your Birth Certificate"
                        } else {
                            contentTv.text = "Birth Certificate"; iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                }


            }
        }

        // Implementation

        override fun onClick(v: View?) {
            onDocumentItemClick.onItemClick(v!!, position_, type)
        }

        fun setOnDocumentItemClick(onDocumentItemClick: OnDocumentItemClick) {
            this.onDocumentItemClick = onDocumentItemClick
        }

        // Private

        private fun icon(icon: Int) = context.resources.getDrawable(icon, null)
    }
}