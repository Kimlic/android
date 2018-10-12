package com.kimlic.stage.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.documents.Status
import com.kimlic.stage.NameItem
import com.kimlic.stage.UserItem
import com.kimlic.utils.AppDoc
import kotlinx.android.synthetic.main.item_user_stage.view.*
import java.util.*

class UserStageAccountAdapter : RecyclerView.Adapter<UserStageAccountAdapter.ContactViewHolder>() {

    // Variables

    private var contactList: List<UserItem> = Collections.synchronizedList(emptyList())
    private lateinit var onUserItemClick: OnUserItemClick

    init {
        setHasStableIds(true)
    }
    // Live

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_stage, parent, false)
        return ContactViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position], position)
        holder.setOnUserItemClick(onUserItemClick)
    }

    // Public

    //@Synchronized
    fun setContacts(contacts: List<UserItem>) {
        this.contactList = contacts
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setOnUserItemClick(onUserItemClick: OnUserItemClick) {
        this.onUserItemClick = onUserItemClick
    }

    // ViewHolder class

    class ContactViewHolder(private val contactView: View, val context: Context) : RecyclerView.ViewHolder(contactView), View.OnClickListener {

        // Variables

        private lateinit var onUserItemClick: OnUserItemClick
        private var position_: Int = 0
        private var type: String = ""
        private var value = ""

        fun bind(item: UserItem, position: Int) {
            position_ = position
            type = item.type
            value = item.value
            with(contactView) {
                warningIv.visibility = if (item.status == Status.UNVERIFIED.state) View.VISIBLE else View.GONE

                if (item.value != "") {
                    contentTv.setTextColor(Color.WHITE)
                    arrowIv.background = resources.getDrawable(R.drawable.ic_white_menu_arrow, null)
                } else {
                    contentTv.setTextColor(resources.getColor(R.color.lightBlue, null))
                    arrowIv.background = resources.getDrawable(R.drawable.ic_blue_menu_arrow, null)
                }

                setOnClickListener(this@ContactViewHolder)

                when (item.type) {
                    "USER_NAME" -> {
                        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

                        if (item.value == "") {
                            iconIv.background = icon(Icons_.NAME_BLUE.icon)
                            contentTv.text = context.getString(R.string.add_your_name)
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.NAME_WHITE.icon)
                            arrowIv.background = resources.getDrawable(R.drawable.ic_white_menu_arrow, null)
                        }

                        arrowIv.visibility = if ((item as NameItem).isClickable) View.VISIBLE else View.INVISIBLE
                    }
                    "risks" -> {
                        if (item.value == "1") {
                            iconIv.background = resources.getDrawable(R.drawable.ic_blue_warning_icon, null); contentTv.text = context.getString(R.string.you_have_security_risks)
                            arrowIv.visibility = View.GONE
                        }
                    }
                    "kim" -> {
                        if (item.value == "0") {
                            iconIv.background = icon(Icons_.KIM_BLUE.icon);contentTv.text = resources.getString(R.string.balance, 0)
                            arrowIv.visibility = View.GONE
                            contentTv.setTextColor(resources.getColor(R.color.lightBlue, null))
                        } else {
                            iconIv.background = icon(Icons_.KIM_WHITE.icon); contentTv.text = resources.getString(R.string.balance, item.value.toInt())
                            arrowIv.visibility = View.GONE
                        }
                    }
                    "email" -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.EMAIL_BLUE.icon);contentTv.text = context.getString(R.string.add_your_email)
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.EMAIL_WHITE.icon)
                            arrowIv.visibility = View.GONE
                        }
                    }
                    "phone" -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.PHONE_BLUE.icon); contentTv.text = context.getString(R.string.add_your_phone)
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.PHONE_WHITE.icon)
                            arrowIv.visibility = View.GONE
                        }
                    }
                    "address" -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.LOCATION_BLUE.icon); contentTv.text = context.getString(R.string.add_your_address)
                        } else {
                            contentTv.text = item.value; iconIv.background = icon(Icons_.LOCATION_WHITE.icon)
                        }
                    }
                    AppDoc.PASSPORT.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_passport)
                        } else {
                            contentTv.text = context.getString(R.string.passport)
                            iconIv.background = icon(Icons_.ID_WHITE.icon)
                        }
                    }
                    AppDoc.ID_CARD.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_id_card)
                        } else {
                            contentTv.text = context.getString(R.string.id_card); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.DRIVERS_LICENSE.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_drivers_license)
                        } else {
                            contentTv.text = context.getString(R.string.drivers_license); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.RESIDENCE_PERMIT_CARD.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_residence_permit)
                        } else {
                            contentTv.text = context.getString(R.string.residence_permit); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.SOCIAL_SECURITY_CARD.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_social_security_card)
                        } else {
                            contentTv.text = context.getString(R.string.social_security_card); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    AppDoc.BIRTH_CERTIFICATE.type -> {
                        if (item.value == "") {
                            iconIv.background = icon(Icons_.ID_BLUE.icon); contentTv.text = context.getString(R.string.add_your_birth_certificate)
                        } else {
                            contentTv.text = context.getString(R.string.birth_certificate); iconIv.background = resources.getDrawable(Icons_.ID_WHITE.icon, null)
                        }
                    }
                    "add" -> {
                        iconIv.background = icon(Icons_.ID_BLUE.icon)
                        contentTv.text = context.getString(R.string.add_new_document)
                    }
                }
            }
        }

        // Implementation

        override fun onClick(v: View?) {
            onUserItemClick.onItemClick(v!!, position_, type, value)
        }

        fun setOnUserItemClick(onUserItemClick: OnUserItemClick) {
            this.onUserItemClick = onUserItemClick
        }

        // Private

        private fun icon(icon: Int) = context.resources.getDrawable(icon, null)
    }
}