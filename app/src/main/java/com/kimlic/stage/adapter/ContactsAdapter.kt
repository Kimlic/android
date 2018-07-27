package com.kimlic.stage.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.db.entity.Contact
import kotlinx.android.synthetic.main.item_stage.view.*

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    // Variables

    private var contactList: List<Contact> = emptyList()
    private lateinit var onStageItemClick: OnStageItemClick

    // Life

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stage, parent, false)
        return ContactsViewHolder(view)
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(contactList.elementAt(position))
        holder.setOnStageItemClick(onStageItemClick)
    }

    // Public

    fun setContactsList(contacts: List<Contact>) {
        val tempList = mutableListOf(Contact(type = "phone"), Contact(type = "email"))
        contacts.forEach {
            if (it.type.equals("phone")) tempList[0] = it
            if (it.type.equals("email")) tempList[1] = it
        }

        this.contactList = tempList
        notifyDataSetChanged()
    }

    fun setOnStageItemClick(onStageItemClick: OnStageItemClick) {
        this.onStageItemClick = onStageItemClick
    }

    // View Holder

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Variables

        private lateinit var onStageItemClick: OnStageItemClick
        private lateinit var contactType: String
        private var approved: Boolean = false

        // Public

        fun bind(itemContact: Contact) {
            contactType = itemContact.type
            approved = itemContact.approved
            with(itemView) {
                setOnClickListener(this@ContactsViewHolder)
                isClickable = itemContact.value.equals("")
                contentTv.setTextColor(if (itemContact.value.equals("")) resources.getColor(R.color.lightBlue, null) else resources.getColor(android.R.color.white, null))
                arrowIv.background = resources.getDrawable(if (itemContact.value.equals("")) Icons_.ARROW_BLUE.icon else Icons_.ARROW_WHITE.icon, null)
                when (itemContact.type) {
                    "phone" -> {
                        iconIv.background = resources.getDrawable(if (itemContact.value.equals("")) Icons_.PHONE_BLUE.icon else Icons_.PHONE_WHITE.icon, null)
                        contentTv.text = if (itemContact.value.equals("")) resources.getString(R.string.phone_number) else itemContact.value
                    }
                    "email" -> {
                        iconIv.background = resources.getDrawable(if (itemContact.value.equals("")) Icons_.EMAIL_BLUE.icon else Icons_.EMAIL_WHITE.icon, null)
                        contentTv.text = if (itemContact.value.equals("")) resources.getString(R.string.email_address) else itemContact.value
                    }
                }
            }
        }

        fun setOnStageItemClick(onStageItemClick: OnStageItemClick) {
            this.onStageItemClick = onStageItemClick
        }

        // OnClick implementation

        override fun onClick(v: View?) {
            onStageItemClick.onClick(v!!, adapterPosition, contactType, approved)
        }
    }
}