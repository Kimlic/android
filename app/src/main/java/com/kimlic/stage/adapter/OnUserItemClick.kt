package com.kimlic.stage.adapter

import android.view.View

interface OnUserItemClick {

    fun onItemClick(view: View, position: Int, type: String, value: String)
}