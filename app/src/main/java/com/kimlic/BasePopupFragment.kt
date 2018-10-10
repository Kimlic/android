package com.kimlic

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.view.WindowManager

abstract class BasePopupFragment : DialogFragment() {

    // Variables

    var activity: BaseActivity? = null

    // Life

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is BaseActivity)
            activity = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)// or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LOW_PROFILE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.window.setBackgroundDrawableResource(R.drawable.rounded_background_fragment)
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }
}