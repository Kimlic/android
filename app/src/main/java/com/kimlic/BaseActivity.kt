package com.kimlic

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.ErrorPopupFragment


abstract class BaseActivity : AppCompatActivity(), View.OnSystemUiVisibilityChangeListener {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onResume() {
        super.onResume()
        if ((application as KimlicApp).wasInBackground) {
            if (Prefs.isPasscodeEnabled) {
                PresentationManager.passcodeFinish(this)
            }
        }
        (application as KimlicApp).stopActivityTransitionTimer()
        hideSystemUI()
    }

    override fun onPause() {
        (application as KimlicApp).startActivityTransitionTimer()
        hideKeyboard()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        //super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onSystemUiVisibilityChange(visibility: Int) {
        hideSystemUI()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }


    fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//
                        or View.SYSTEM_UI_FLAG_LOW_PROFILE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE//
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//
                        or View.SYSTEM_UI_FLAG_FULLSCREEN//
                )
    }

    // Public

    fun showToast(text: String) {
        if (text.isNotEmpty()) Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }


    fun showSoftKeyboard(view: View) {
        view.requestFocus()
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    protected fun hideKeyboard() {
        val view = currentFocus ?: return
        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        hideSystemUI()
    }

    fun getStringValue(resId: Int): String = KimlicApp.applicationContext().getString(resId)

    fun errorPopup(error: String? = getString(R.string.error)) {
        val bundle = Bundle()
        bundle.putString(AppConstants.ERROR_DESCRIPTION.key, error)
        val errorFragment = ErrorPopupFragment.newInstance(bundle)
        errorFragment.show(this.supportFragmentManager, ErrorPopupFragment.FRAGMENT_KEY)
    }

    // Private

    private fun setupUI() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    open fun showFragment(containerViewId: Int, fragment: Fragment, tag: String, title: String = "") {
        if (supportActionBar != null && title != "")
            supportActionBar?.title = title

        val transaction = this.supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, tag)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    open fun showPopupImmersive(title: String = "", message: String) {
        val builder = getImmersivePopupBuilder()

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog?.dismiss() }.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    fun getImmersivePopupBuilder(): AlertDialog.Builder {
        return object : AlertDialog.Builder(this) {

            // Live

            override fun create(): AlertDialog {
                val d = super.create()
                d.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                d.window.decorView.systemUiVisibility = (
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
                return d
            }
        }
    }
}