package com.kimlic.managers

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.auth.CreateTouchIdActivity
import com.kimlic.auth.LoginActivity
import com.kimlic.splash_screen.SplashScreenActivity
import com.kimlic.tutorial.TutorialActivity
import java.util.*

object PresentationManager {

    private val TAG = this.javaClass.simpleName

    fun splash(presentrer: BaseActivity) {
        present(presenter = presentrer, className = SplashScreenActivity::class.java, isStarting = true)
    }

    fun tutorials(presenter: BaseActivity) {
        present(presenter = presenter, className = TutorialActivity::class.java, isStarting = true)
    }

    fun toucthIdCreate(presenter: BaseActivity) {
        present(presenter = presenter, className = CreateTouchIdActivity::class.java, isStarting = false)
    }

    fun loginActivity(presenter: BaseActivity) {
        present(presenter = presenter, className = LoginActivity::class.java, isStarting = true)
    }

    // private

    private fun present(presenter: BaseActivity, className: Class<out Activity>, isStarting: Boolean = false, params: HashMap<String, String>? = null) {
        val intent = Intent(presenter, className)
        params.let { it?.forEach { (key, value) -> intent.putExtra(key, value) } }

        if (isStarting) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("starting", true)
            presenter.finish()
        }

        presenter.startActivity(intent)
    }

    private fun presentFragment(presenter: BaseActivity, fragment: Fragment, sourceTag: String, targetTag: String) {
        val fragmentManager = presenter.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction!!.replace(R.id.fragment_container, fragment, targetTag)
        transaction.commit()
    }
}