package com.kimlic.stage

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.preferences.Prefs
import kotlinx.android.synthetic.main.activity_stage.*

class StageActivity : BaseActivity() {

    // Variables

    private lateinit var userStageFragment: UserStageFragment
    private lateinit var accountsStageFragment: AccountsStageFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        risks()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    // Private

    private fun setupUI() {
        initFragments()
        setupListners()
        profileBt.isSelected = true
        replaceStageFragment()
    }

    private fun setupListners() {
        profileBt.setOnClickListener {
            profileBt.isSelected = true; accountsBt.isSelected = false; profileLineV.visibility = View.VISIBLE; accountsLineV.visibility = View.INVISIBLE
            replaceStageFragment()
        }
        accountsBt.setOnClickListener {
            accountsBt.isSelected = true; profileBt.isSelected = false; accountsLineV.visibility = View.VISIBLE; profileLineV.visibility = View.INVISIBLE
            replaceAccountsFragment()
        }
        scanBt.setOnClickListener { showToast("Scan button is pressed") }
    }
    private fun initFragments() {
        userStageFragment = UserStageFragment.newInstance()
        accountsStageFragment = AccountsStageFragment.newInstance()
    }

    private fun risks() {
        if (!Prefs.isPasscodeOffered || !Prefs.isRecoveryOffered) {
            val bundle = Bundle()

            if (Prefs.isPasscodeEnabled) Prefs.isPasscodeOffered = true

            bundle.putBoolean("passcode", Prefs.isPasscodeOffered)
            bundle.putBoolean("recovery", Prefs.isRecoveryOffered)

            val risksFragment = RisksFragment.newInstance(bundle)
            risksFragment.show(supportFragmentManager, RisksFragment.FRAGMENT_KEY)
        }
    }

    // Helpers

    private fun replaceStageFragment(): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        ft.replace(R.id.container, userStageFragment, UserStageFragment.FRAGMENT_KEY).commit()
        return true
    }

    private fun replaceAccountsFragment(): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        ft.replace(R.id.container, accountsStageFragment, AccountsStageFragment.FRAGMENT_KEY).commit()
        return true
    }
}
