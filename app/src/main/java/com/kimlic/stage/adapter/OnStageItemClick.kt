package com.kimlic.stage.adapter

import android.view.View

interface OnStageItemClick {
    fun onClick(view: View, position: Int, type: String, aprooved: Boolean)
}