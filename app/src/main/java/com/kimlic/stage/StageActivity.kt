package com.kimlic.stage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
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

    // Private

    private fun setupUI() {
        initFragments()
        setupListners()
        profileBt.isSelected = true
        replaceFragment(userStageFragment, UserStageFragment.FRAGMENT_KEY)
    }

    private fun setupListners() {
        profileBt.setOnClickListener {
            profileBt.isSelected = true; accountsBt.isSelected = false
            replaceFragment(userStageFragment, UserStageFragment.FRAGMENT_KEY)
        }
        accountsBt.setOnClickListener {
            accountsBt.isSelected = true; profileBt.isSelected = false
            replaceFragment(accountsStageFragment, AccountsStageFragment.FRAGMENT_KEY) }
    }

    private fun initFragments() {
        userStageFragment = UserStageFragment.newInstance()
        accountsStageFragment = AccountsStageFragment.newInstance()
    }

    private fun risks() {
        if (!Prefs.isPasscodeOffered || !Prefs.isRecoveryOffered) {
            val bundle = Bundle()

            bundle.putBoolean("passcode", Prefs.isPasscodeOffered)
            bundle.putBoolean("recovery", Prefs.isRecoveryOffered)

            val risksFragment = RisksFragment.newInstance(bundle)
            risksFragment.show(supportFragmentManager, RisksFragment.FRAGMENT_KEY)
        }
    }

    // Helpers

    private fun replaceFragment(newFragment: Fragment, tag: String): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment, tag).commit()
        return true
    }
}
