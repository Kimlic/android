package com.kimlic.stage.adapter

import android.graphics.drawable.PictureDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.kimlic.R
import com.kimlic.db.entity.Company
import com.kimlic.utils.svg.GlideApp
import com.kimlic.utils.svg.SvgSoftwareLayerSetter
import kotlinx.android.synthetic.main.item_account_rp.view.*

class CompanyAdapter : RecyclerView.Adapter<CompanyAdapter.CompanyHolder>() {

    // Variables

    private var companies: List<Company> = emptyList()
    private lateinit var onAccountItemClick: OnAccountItemClick

    // Live

    override fun getItemCount() = companies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyHolder {
        val accountView = LayoutInflater.from(parent.context).inflate(R.layout.item_account_rp, parent, false)
        return CompanyHolder(accountView)
    }

    override fun onBindViewHolder(holder: CompanyHolder, position: Int) {
        holder.setOnAccountItemClick(onAccountItemClick)
        holder.bind(companies[position], position)
    }

    // Public

    fun setAccountsList(accounts: List<Company>) {
        this.companies = accounts
        notifyDataSetChanged()
    }

    fun setOnAccountItemClick(onAccountItemClick: OnAccountItemClick) {
        this.onAccountItemClick = onAccountItemClick
    }

    // View Holder class

    class CompanyHolder(private val accountView: View) : RecyclerView.ViewHolder(accountView), View.OnClickListener {

        // Variables

        private var mPosition: Int = 0
        private lateinit var onAccountItemClick: OnAccountItemClick

        // Live

        override fun onClick(v: View?) = onAccountItemClick.onClick(position = mPosition)

        fun setOnAccountItemClick(onAccountItemClick: OnAccountItemClick) {
            this.onAccountItemClick = onAccountItemClick
        }

        // Public

        fun bind(company: Company, position: Int) {
            this.mPosition = position

            with(accountView) {
                rootRl.setBackgroundResource(R.drawable.background_gradient_white)
                rpTitleTv.text = company.name
                rpSubtitleTv.text = company.name
                setOnClickListener(this@CompanyHolder)
            }

            GlideApp.with(accountView.context)
                    .`as`(PictureDrawable::class.java)
                    //.placeholder(R.drawable.image_loading)
                    //.error(R.drawable.image_error)
                    .transition(withCrossFade())
                    .listener(SvgSoftwareLayerSetter())
                    .load(company.logo)
                    .into(accountView.rpLogoIv)
        }
    }
}