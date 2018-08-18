package com.kimlic

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.kimlic.utils.AppConstants
import com.kimlic.utils.ErrorPopupFragment

abstract class BaseActivity : AppCompatActivity() {

    // Companion

    companion object {
        val TAG = this::class.java.simpleName
    }

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
    }

    fun getStringValue(resId: Int): String = KimlicApp.applicationContext().getString(resId)

    fun errorPopup(error: String? = getString(R.string.error)) {
        val bundle = Bundle()
        bundle.putString(AppConstants.errorDescription.key, error)
        val errorFragment = ErrorPopupFragment.newInstanse(bundle)
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

    open fun showPopup(title: String = "", message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog?.dismiss() }.setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    fun rotateBitmap(source: Bitmap, angel: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angel)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}