package com.kimlic

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.kimlic.profile.ProfileViewModel

abstract class BaseFragment : Fragment() {

    // Variables

    var activity: BaseActivity? = null
    protected lateinit var model: ProfileViewModel

    // Life

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        if (context is BaseActivity)
            activity = context
    }

    override fun onDetach() {
        super.onDetach()

        activity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setVerticlal()
    }

    // Public

    fun showToast(text: String) {
        if (text.length > 0) Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }
    }

    fun presentFragment(presenter: BaseFragment, fragment: Fragment, sourceTag: String, targetTag: String) {
        val manager = presenter.activity!!.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, targetTag)
        transaction.addToBackStack(sourceTag)
        transaction.commit()
    }

    // Private

    private fun setVerticlal() {
        getActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}