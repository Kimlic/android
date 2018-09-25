package com.kimlic.stage.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.db.entity.Account
import kotlinx.android.synthetic.main.item_account_rp.view.*

class AccountAdapter : RecyclerView.Adapter<AccountAdapter.AccountHolder>() {

    // Variables

    private var accounts: List<Account> = emptyList()
    private lateinit var onAccountItemClick: OnAccountItemClick

    // Live

    override fun getItemCount() = accounts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val accountView = LayoutInflater.from(parent.context).inflate(R.layout.item_account_rp, parent, false)
        return AccountHolder(accountView)
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        holder.setOnAccountItemClick(onAccountItemClick)
        holder.bind(accounts[position], position)
    }

    // Public

    fun setAccountsList(accounts: List<Account>) {
        this.accounts = accounts
        notifyDataSetChanged()
    }

    fun setOnAccountItemClick(onAccountItemClick: OnAccountItemClick) {
        this.onAccountItemClick = onAccountItemClick
    }

    // View Holder class

    class AccountHolder(private val accountView: View) : RecyclerView.ViewHolder(accountView), View.OnClickListener {

        // Variables

        private var mPosition: Int = 0
        private lateinit var onAccountItemClick: OnAccountItemClick

        // Live

        override fun onClick(v: View?) = onAccountItemClick.onClick(position = mPosition)

        fun setOnAccountItemClick(onAccountItemClick: OnAccountItemClick) {
            this.onAccountItemClick = onAccountItemClick
        }

        // Public

        fun bind(account: Account, position: Int) {
            this.mPosition = position

            with(accountView) {
                rootRl.setBackgroundResource(R.drawable.background_gradient_white)
                rpTitleTv.text = account.name
                rpSubtitleTv.text = account.name
                setOnClickListener(this@AccountHolder)
            }
        }
    }
}