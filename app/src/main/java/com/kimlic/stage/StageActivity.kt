package com.kimlic.stage

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.integration.android.IntentIntegrator
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.scanner.ScannerActivity
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.activity_stage.*

class StageActivity : BaseActivity() {

    // Constants

    companion object {
        private const val SCAN_REQUEST_CODE = 1100
        const val SECURITY_REQUEST_CODE = 151
    }

    // Variables

    private val intentFilter = IntentFilter(AppConstants.DETAILS_BROADCAST_ACTION.key)
    private lateinit var userStageFragment: UserStageFragment
    private lateinit var accountsStageFragment: AccountsStageFragment
    private lateinit var model: ProfileViewModel
    private lateinit var companyModel: CompanyDetailsViewModel
    private lateinit var receiver: BroadcastReceiver

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage)
        registerRedDotReceiver()

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        companyModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        risks()
        manageRedDot(Prefs.newCompanyAccepted)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SCAN_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }

                val result = IntentIntegrator.parseActivityResult(resultCode, data)
                if (result.contents != null) {
                    val urlSplitted = result.contents.split("?").first()
                    PresentationManager.account(this, url = urlSplitted)
                }
            }

            SECURITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK)
                    if (!Prefs.isTouchEnabled) PresentationManager.touchCreate(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    // Public

    fun hideRedButton() {
        Prefs.newCompanyAccepted = false
        manageRedDot(Prefs.newCompanyAccepted)
    }

    // Private

    private fun setupUI() {
        Log.d("TAGACCOUNT", "account address - ${Prefs.currentAccountAddress}")
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { Log.d("TAGACCOUNT", "accountToken = ${it.token}") }
        initFragments()
        lifecycle.addObserver(model)
        lifecycle.addObserver(companyModel)
        lifecycle.addObserver(userStageFragment)
        setupListeners()
        profileBt.isSelected = true
        replaceStageFragment()
    }

    private fun setupListeners() {
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

    fun risks() {
        if (!Prefs.isPasscodeEnabled || !Prefs.isRecoveryEnabled) {

            if (supportFragmentManager.findFragmentByTag("risks") != null) return

            val bundle = Bundle()
            val risksFragment = RisksFragment.newInstance(bundle)
            bundle.putBoolean("passcode", Prefs.isPasscodeEnabled)
            bundle.putBoolean("recovery", Prefs.isRecoveryEnabled)
            risksFragment.show(supportFragmentManager, "risks")
        }
    }

    // Helpers

    private fun registerRedDotReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Prefs.newCompanyAccepted = true
                manageRedDot(Prefs.newCompanyAccepted)// TODO manage redDot!!!
            }
        }
        registerReceiver(receiver, intentFilter)
    }

    private fun replaceStageFragment(): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        ft.replace(R.id.container, userStageFragment, UserStageFragment.FRAGMENT_KEY).commit()
        ft.addToBackStack(null)
        return true
    }

    private fun replaceAccountsFragment(): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        ft.replace(R.id.container, accountsStageFragment, AccountsStageFragment.FRAGMENT_KEY).commit()
        ft.addToBackStack(null)
        return true
    }

    fun manageRedDot(visible: Boolean) {
        redCircleBt.visibility = if (visible) View.VISIBLE else View.GONE
    }
}