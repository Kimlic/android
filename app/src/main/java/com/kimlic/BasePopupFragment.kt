package com.kimlic

import android.content.Context
import android.support.v4.app.DialogFragment

abstract class BasePopupFragment : DialogFragment(){

    // Variables

    var activity: BaseActivity? = null

    // Life

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is BaseActivity)
            activity = context
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(R.drawable.rounded_background_fragment)
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }
}