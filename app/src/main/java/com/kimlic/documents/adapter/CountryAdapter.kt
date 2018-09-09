package com.kimlic.documents.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kimlic.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_country.view.*

class CountryAdapter(context: Context) : ArrayAdapter<String>(context, R.layout.item_country) {

    // Variables

    private var countryList: List<String> = listOf()
    var selectedPosition: Int = -1

    // Public

    fun setCountries(countries: List<String>) {
        countryList = countries
        notifyDataSetChanged()
    }

    // Life

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val path = "file:///android_asset/flags/${countryList[position]}.png"
        val view: View = convertView ?: LayoutInflater.from(parent!!.context).inflate(R.layout.item_country, parent, false)

        with(view) {
            countryTv.text = countryList[position]
            doneIv.visibility = View.INVISIBLE
            countryTv.setTextColor(resources.getColor(R.color.lightBlue, null))

            if (position == selectedPosition) {
                doneIv.visibility = View.VISIBLE
                countryTv.setTextColor(Color.WHITE)
            }

            Picasso.get()
                    .load(path)
                    .noFade()
                    .into(flagIv)
        }
        return view
    }

    override fun getCount() = countryList.size
}