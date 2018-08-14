package com.kimlic.stage

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.scanner.ScannerActivity
import kotlinx.android.synthetic.main.activity_stage.*

class StageActivity : BaseActivity() {

    val SCAN_REQUEST_CODE = 1100
    val SECURITY_REQUESTCODE = 151
    private val GOOGLE_SIGNE_IN_REQUEST_CODE = 103

    // Variables

    private lateinit var userStageFragment: UserStageFragment
    private lateinit var accountsStageFragment: AccountsStageFragment
    private lateinit var model: ProfileViewModel
   // private lateinit var googleSigneIn

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

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SCAN_REQUEST_CODE -> {
                val result = IntentIntegrator.parseActivityResult(resultCode, data)
                if (result.contents != null) {
                    val url = result.contents
                    Log.d("TAGSCANNER", "Scanned")
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show()
                    PresentationManager.vendors(this, url = url)
                }
            }

            SECURITY_REQUESTCODE -> {
                Log.d("TAGINSTAGE", "from fragment, touch is enabled " + Prefs.isTouchEnabled)
                if (!Prefs.isTouchEnabled) PresentationManager.touchCreate(this)
            }
        }
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        lifecycle.addObserver(model)
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
        scanBt.setOnClickListener {
            IntentIntegrator(this).setOrientationLocked(true).setRequestCode(SCAN_REQUEST_CODE).setCaptureActivity(ScannerActivity::class.java).initiateScan()
        }
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
