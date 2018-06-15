package com.kimlic.settings

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kimlic.R
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.item_setting_intent.view.*
import kotlinx.android.synthetic.main.item_setting_switch.view.*

class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    // Variables

    private var settingsList: List<Setting> = emptyList()
    lateinit var onItemClick: OnItemClick

    // Life

    override fun getItemViewType(position: Int) = settingsList[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view =
                if (viewType == 1)
                    return SettingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_setting_switch, parent, false))
                else SettingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_setting_intent, parent, false))
        return view
    }

    override fun getItemCount() = settingsList.count()

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(settingsList.elementAt(position))
        holder.setOnItemClick(onItemClick)
    }

    // Public

    fun setSettingsList(settings: List<Setting>) {
        this.settingsList = settings
        notifyDataSetChanged()
    }

    // ViewHolder

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Variables

        private lateinit var onItemClick: OnItemClick

        // Public

        fun bind(itemSetting: Setting) {
            with(itemView) {
                setOnClickListener(this@SettingsViewHolder)
                when (itemSetting.type) {
                    AppConstants.settingSwitch.intKey -> {
                        titleSwitchTv.text = itemSetting.title
                        summaryTv.text = itemSetting.summary
                        selector.isChecked = itemSetting.state
                        tag = itemSetting.tag
                        warningIv.visibility = if (!itemSetting.state) ImageView.VISIBLE else ImageView.INVISIBLE
                    }
                    AppConstants.settingIntent.intKey -> {
                        if (!itemSetting.summary.equals("")) {
                            summaryITv.visibility = View.VISIBLE
                            summaryITv.text = Editable.Factory.getInstance().newEditable(itemSetting.summary)
                        }

                        titleTv.text = itemSetting.title
                        tag = itemSetting.tag
                    }
                }
            }
        }

        fun setOnItemClick(onItemClick: OnItemClick) {
            this.onItemClick = onItemClick
        }

        // OnClick

        override fun onClick(v: View?) {
            if (itemView.selector != null) {
                if (itemView.selector.isChecked)
                    itemView.selector.isChecked = false
                else
                    itemView.selector.isChecked = true
            }

            onItemClick.onClick(v!!, adapterPosition)
        }
    }
}



