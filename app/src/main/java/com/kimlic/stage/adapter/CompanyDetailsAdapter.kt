package com.kimlic.stage.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.stage.DetailsItem
import com.kimlic.utils.time_converter.TimeZoneConverter
import kotlinx.android.synthetic.main.item_account_document.view.*
import java.util.*

class CompanyDetailsAdapter : RecyclerView.Adapter<CompanyDetailsAdapter.ContactViewHolder>() {

    // Variables

    private var contactList: List<DetailsItem> = Collections.synchronizedList(emptyList())

    init {
        setHasStableIds(true)
    }
    // Live

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pr_details, parent, false)
        return ContactViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    // Public

    //@Synchronized
    fun setDetails(contacts: List<DetailsItem>) {
        this.contactList = contacts
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // ViewHolder class

    class ContactViewHolder(private val companyView: View, val context: Context) : RecyclerView.ViewHolder(companyView) {

        // Variables

        fun bind(item: DetailsItem) {

            with(companyView) {
                contentTv.setTextColor(Color.WHITE); isClickable = false; isFocusableInTouchMode = false; isFocusable = false

                when (item.type) {
                    "date" -> {
                        val dateString = TimeZoneConverter().convertSecondsToDateString(item.value.toLong())
                        contentTv.text = context.getString(R.string.application, dateString)
                        iconIv.background = icon(Icons_.DATE_WHITE.icon)
                    }
                    "name" -> {
                        contentTv.text = item.value; iconIv.background = icon(Icons_.NAME_WHITE.icon)
                    }
                    "phone" -> {
                        contentTv.text = item.value; iconIv.background = icon(Icons_.PHONE_WHITE.icon)
                    }
//                    AppDoc.PASSPORT.type -> {
//                        if (item.value == "") {
//                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_passport)
//                        } else {
//                            contentTv.text = context.getString(R.string.passport); iconIv.background = icon(Icons_.ID_WHITE.icon)
//                        }
//                    }
//                    AppDoc.ID_CARD.type -> {
//                        if (item.value == "") {
//                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_id_card)
//                        } else {
//                            contentTv.text = context.getString(R.string.id_card); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
//                        }
//                    }
//                    AppDoc.DRIVERS_LICENSE.type -> {
//                        if (item.value == "") {
//                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_drivers_license)
//                        } else {
//                            contentTv.text = context.getString(R.string.drivers_license); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
//                        }
//                    }
//                    AppDoc.RESIDENCE_PERMIT_CARD.type -> {
//                        if (item.value == "") {
//                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_residence_permit)
//                        } else {
//                            contentTv.text = context.getString(R.string.residence_permit); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
//                        }
//                    }
//                    AppDoc.SOCIAL_SECURITY_CARD.type -> {
//                        if (item.value == "") {
//                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_social_security_card)
//                        } else {
//                            contentTv.text = context.getString(R.string.social_security_card); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
//                        }
//                    }
//                    AppDoc.BIRTH_CERTIFICATE.type -> {
//                        if (item.value == "") {
//                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_birth_certificate)
//                        } else {
//                            contentTv.text = context.getString(R.string.birth_certificate); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
//                        }
//                    }
//                    "addDocument" -> {
//                        iconIv.background = icon(Icons_.ID_BLUE.icon);
//                        contentTv.text = "Add Your Document"
//                    }
                }
            }
        }

        // Private

        private fun icon(icon: Int) = context.resources.getDrawable(icon, null)
    }
}