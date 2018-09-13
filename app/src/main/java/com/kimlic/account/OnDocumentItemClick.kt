package com.kimlic.account

import android.view.View

interface OnDocumentItemClick {

    fun onItemClick(view: View, position: Int, type: String)
}