package com.kimlic.passcode

import android.app.Activity
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.KimlicApp
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.MessageCallback
import kotlinx.android.synthetic.main.activity_passcode.*


class PasscodeActivity : BaseActivity(), View.OnClickListener {

    // Binding

    @BindViews(R.id.passcode0Bt, R.id.passcodeBt1, R.id.passcodeBt2, R.id.passcodeBt3, R.id.passcodeBt4, R.id.passcodeBt5, R.id.passcodeBt6, R.id.passcodeBt7, R.id.passcodeBt8, R.id.passcodeBt9)
    lateinit var mBtnList: List<@JvmSuppressWildcards Button>

    @BindViews(R.id.dot1Bt, R.id.dot2Bt, R.id.dot3Bt, R.id.dot4Bt, R.id.dot5Bt, R.id.dot6Bt)
    lateinit var mDotList: List<@JvmSuppressWildcards Button>

    // Variables

    private var passcode: String
    private var mode = ""
    private var firstInput: Boolean
    private lateinit var passcodeConfirm: String
    private lateinit var action: String

    private var intervals = arrayOf(30, 60, 300, 900, 1800, 3600, 216000)// 0.5m, 1m, 5m, 15m, 30m, 1 hour, 6 hours

    // Init

    init {
        passcode = ""
        firstInput = false
    }

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)
        ButterKnife.bind(this)

        setupDefaults()
        setupUI()

    }

    override fun onBackPressed() {
        when {
            passcode.isNotEmpty() -> deletePasscode()
            mode == "unlock" -> moveTaskToBack(true)
            else -> super.onBackPressed()
        }
    }

    // OnClick

    override fun onClick(v: View?) {
        val digit: String = (v as Button).text.toString()

        if (passcode.length < mDotList.size) passcode = passcode.plus(digit)

        showDots(passcode)
    }

    // Private

    private fun setupUI() {
        mBtnList.forEach { it.setOnClickListener(this) }
        setupTextSwitcher()

        action = intent?.extras?.getString("action", "").orEmpty()

        if (intent?.extras?.containsKey("mode")!!) mode = intent!!.extras!!.getString("mode", "finish")

        when (action) {
            "unlock" -> {
                setupUIUnlock()

                if (Prefs.timeToUnlock > System.currentTimeMillis())
                    lockedInfoFragment(Prefs.timeToUnlock)
                else
                    if (Prefs.isTouchEnabled)
                        proposeFingerprintPopup()

            }
            "set" -> setupUIPasscode()
            "change" -> setupUIChange()
            "disable" -> setupUIDisable()
            "" -> throw Exception("Wrong passcode action")
        }

        passcodeDeleteBt.setOnClickListener { deletePasscode() }
        cancelTv.setOnClickListener {
            if (action == "unlock")
                moveTaskToBack(true)
            else
                finish()
        }
        //passcodeOkBt.setOnClickListener { usePasscode(action) }
    }

    private fun proposeFingerprintPopup() {
        val fragment = FingerprintProposeFragment.newInstance()
        fragment.setCallback(object : MessageCallback {
            override fun callback(message: String) {
                when (message) {
                    "success" -> {
                        if (mode == "unlock_finish") {
                            (application as KimlicApp).wasInBackground = false
                            (application as KimlicApp).isFirstTime = false
                            fragment.dismissAllowingStateLoss()
                        } else PresentationManager.stage(this@PasscodeActivity)
                    }
                    "error" -> {
                    }
                }
            }
        })
        fragment.show(supportFragmentManager, FingerprintProposeFragment.FRAGMENT_KEY)
    }

    private fun lockedInfoFragment(unlockTime: Long) {
        val fragment = LockedInfoFragment.newInstance(Bundle().apply { this.putLong(AppConstants.UNLOCK_TIME.key, unlockTime) })

        fragment.setCallback(object : MessageCallback {
            override fun callback(message: String) {
                when (message) {
                    AppConstants.LATER.key -> {
                        moveTaskToBack(true)
                    }
                    AppConstants.TIME.key -> {
                        Prefs.tryCount = 0
                        fragment.dismissAllowingStateLoss()
                        if (Prefs.isTouchEnabled) proposeFingerprintPopup()
                    }
                }
            }
        })
        fragment.show(supportFragmentManager, LockedInfoFragment.FRAGMENT_KEY)
    }

    private fun deletePasscode() {
        if (passcode.isEmpty()) return

        passcode = passcode.substring(0, passcode.length - 1)
        showDots(passcode)
    }

    private fun setupDefaults() {
        passcode = ""
    }

    // Setup UI

    private fun setupUIPasscode() {
        mBtnList.forEach { it.isClickable = true }
        titleTs.setText(getString(R.string.create_your_passcode))
        subtitleTs.setText(getString(R.string.You_have_to_enter_passcode_every_time_you_want_to_open))
        showDots()
    }

    private fun setupUIChange() {
        mBtnList.forEach { it.isClickable = true }
        titleTs.setText(getString(R.string.enter_old_passcode))
        subtitleTs.setText(getString(R.string.enter_your_old_passcode))
        showDots()
    }

    private fun setupUISecondPasscode() {
        mBtnList.forEach { it.isClickable = true }
        titleTs.setText(getString(R.string.confirm_passcode))
        subtitleTs.setText(getString(R.string.enter_the_passcode_againe))
        showDots()
    }

    private fun setupUIUnlock() {
        mBtnList.forEach { it.isClickable = true }
        titleTs.setText(getString(R.string.enter_passcode))
        subtitleTs.setText(getString(R.string.enter_your_passcode_to_proceed))
        showDots()
    }

    private fun setupUITryAgain() {
        mBtnList.forEach { it.isClickable = true }
        titleTs.setText(getString(R.string.passcode_didnt_match))
        //passcodeOkBt.isEnabled = false
        firstInput = false
        blinkDots()
    }

    private fun setupUIDisable() {
        mBtnList.forEach { it.isClickable = true }
        titleTs.setText(getString(R.string.enter_passcode))
        subtitleTs.setText(getString(R.string.enter_your_passcode_to_proceed))
        showDots()
    }

    // Use passcode

    private fun usePasscode(action: String) {
        when (action) {
            "unlock" -> unlock()
            "set" -> set()
            "change" -> change()
            "disable" -> disable()
        }
    }

    private fun unlock() {
        if (Prefs.passcode == passcode) {
            Prefs.currentLockInterval = 0
            Prefs.tryCount = 0
            if (mode == "unlock_finish") {
                (application as KimlicApp).wasInBackground = false
                (application as KimlicApp).isFirstTime = false
                finish()
            } else {
                (application as KimlicApp).wasInBackground = false
                (application as KimlicApp).isFirstTime = false
                PresentationManager.stage(this@PasscodeActivity)
                finish()
            }
        } else {
            Prefs.tryCount += 1
            Log.d("LOGTRY", "tryCount = ${Prefs.tryCount}")
            if (Prefs.tryCount > 3) {
                Prefs.timeToUnlock = System.currentTimeMillis() + intervals[Prefs.currentLockInterval] * 1000
                if (Prefs.currentLockInterval < intervals.size - 1) Prefs.currentLockInterval++
                lockedInfoFragment(Prefs.timeToUnlock)
            }
            setupUIUnlock()
        }
    }

    private fun set() {
        if (!firstInput) {
            firstInput = true
            passcodeConfirm = passcode
            setupUISecondPasscode()
        } else if (passcodeConfirm == passcode) {
            Prefs.passcode = passcode
            Prefs.isPasscodeEnabled = true
            successful()
        } else setupUITryAgain()
    }

    private fun change() {
        if (passcode == Prefs.passcode) {
            action = "set"
            setupUIPasscode()
        } else
            setupUITryAgain()
    }

    private fun disable() {
        if (passcode == Prefs.passcode) {
            Prefs.isPasscodeEnabled = false
            Prefs.isTouchEnabled = false
            finish()
        } else
            setupUITryAgain()

    }

    private fun blinkDots() {
        Handler().post {
            mDotList.forEach {
                it.setBackgroundResource(R.drawable.button_dots_shape_oval_color_blink)
                (it.background as Animatable).start()
            }
        }

        Handler().postDelayed({
            mDotList.forEach { it.setBackgroundResource(R.drawable.button_dots_shape_oval) }
            showDots()
        }, 500)
    }

    private fun showDots(passcode: String = "") {
        if (passcode == "") this.passcode = passcode
        val passLength = passcode.length

        for (dot in mDotList) {
            val tag = dot.tag.toString().toInt()
            dot.isPressed = tag <= passLength
        }

        passcodeDeleteBt.visibility = if (passLength > 0) View.VISIBLE else View.INVISIBLE
        //passcodeOkBt.isEnabled = this.passcode.length == 4

        if (this.passcode.length == mDotList.size) {
            mBtnList.forEach { it.isClickable = false }

            object : CountDownTimer(250, 250) {
                override fun onFinish() {
                    usePasscode(action)
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()
        }
    }

    // Text switcher

    private fun setupTextSwitcher() {
        titleTs.setFactory { TextView(ContextThemeWrapper(this@PasscodeActivity, R.style.TitleStyle), null, 0) }
        subtitleTs.setFactory { TextView(ContextThemeWrapper(this@PasscodeActivity, R.style.SubtitleStyle), null, 0) }

        val inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val outAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        inAnim.duration = 150
        outAnim.duration = 150

        titleTs.inAnimation = inAnim
        titleTs.outAnimation = outAnim

        subtitleTs.inAnimation = inAnim
        subtitleTs.outAnimation = outAnim
    }

    private fun successful() {
        val fragment = PasscodeSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
        fragment.show(supportFragmentManager, PasscodeSuccessfulFragment.FRAGMENT_KEY)
    }
}