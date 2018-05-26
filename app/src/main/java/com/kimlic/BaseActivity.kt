package com.kimlic

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast

abstract class BaseActivity : AppCompatActivity() {

    // Companion

    companion object {
        val TAG = this::class.java.simpleName
    }

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
    }

    // Public

    fun showToast(text: String) {
        if (text.length > 0) Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    // Private

    private fun setupUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    open fun showFragment(containerViewId: Int, fragment: Fragment, tag: String, title: String = "") {
        if (supportActionBar != null && title != "")
            supportActionBar?.setTitle(title)

        val transaction = this.supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, tag)
        transaction.addToBackStack(tag)
        transaction.commit()
    }
}
