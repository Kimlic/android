package com.kimlic.managers

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.kimlic.BaseActivity
import com.kimlic.stage.StageActivity
import com.kimlic.R
import com.kimlic.SignupRecoveryActivity
import com.kimlic.auth.LoginActivity
import com.kimlic.auth.TouchIdActivity
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.phone.PhoneActivity
import com.kimlic.phone.PhoneVerifyActivity
import com.kimlic.phrase.PhraseGenerateActivity
import com.kimlic.phrase.PhraseVerifyActivity
import com.kimlic.profile_details.NameActivity
import com.kimlic.profile_details.UserProfileActivity
import com.kimlic.recovery.AccountRecoveryActivity
import com.kimlic.settings.SettingsActivity
import com.kimlic.splash_screen.SplashScreenActivity
import com.kimlic.terms.TermsActivity
import com.kimlic.tutorial.TutorialActivity
import com.kimlic.video_id_verification.DocumentVerifyActivity
import com.kimlic.video_id_verification.DocumentVerifyChooseActivity
import java.util.*

object PresentationManager {

    private val TAG = this.javaClass.simpleName

    fun splash(presentrer: BaseActivity) {
        present(presenter = presentrer, className = SplashScreenActivity::class.java, isStarting = true)
    }

    fun tutorials(presenter: BaseActivity) {
        present(presenter = presenter, className = TutorialActivity::class.java, isStarting = true)
    }

    fun passcode(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "set")
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = false, params = params)
    }

    fun passcodeUnlock(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "unlock")
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = true, params = params)
    }

    fun passcodeDisable(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "disable")
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = false, params = params)
    }

    fun passcodeChange(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "change")
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = false, params = params)
    }

    fun touchCreate(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "create")
        present(presenter = presenter, className = TouchIdActivity::class.java, isStarting = false, params = params)
    }

    fun touchDisable(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "disable")
        present(presenter = presenter, className = TouchIdActivity::class.java, isStarting = false, params = params)
    }

    // causes passphrase generation
    fun recoveryEnable(presenter: BaseActivity) {
        present(presenter = presenter, className = PhraseGenerateActivity::class.java, isStarting = false)
    }

    fun phraseVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = PhraseVerifyActivity::class.java)
    }

    // TODO probably will be changed
    fun recoveryDisable(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "disable")
        params.put("content", "terms")
        present(presenter = presenter, className = PhraseVerifyActivity::class.java, params = params)
    }

    fun loginActivity(presenter: BaseActivity) {
        present(presenter = presenter, className = LoginActivity::class.java, isStarting = true)
    }

    fun phoneNumber(presenter: BaseActivity) {
        present(presenter = presenter, className = PhoneActivity::class.java, isStarting = true)
    }

    fun phoneNumberVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = PhoneVerifyActivity::class.java, isStarting = false)
    }

    fun name(presenter: BaseActivity) {
        present(presenter = presenter, className = NameActivity::class.java, isStarting = false)
    }

    fun recovery(presenter: BaseActivity) {
        present(presenter = presenter, className = AccountRecoveryActivity::class.java, isStarting = true)
    }

    fun documentChooseVerify(presenter: BaseActivity){
        present(presenter = presenter, className = DocumentVerifyChooseActivity::class.java, isStarting = false)
    }

    fun documentVerify(presenter: BaseActivity){
        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false)
    }

    fun signupRecovery(presenter: BaseActivity) {
        present(presenter = presenter, className = SignupRecoveryActivity::class.java, isStarting = true)
    }

    fun passhraseCreate(presenter: BaseActivity) {
        present(presenter = presenter, className = PhraseGenerateActivity::class.java)
    }

    // Not used?
    fun userProfile(presenter: BaseActivity) {
        present(presenter = presenter, className = UserProfileActivity::class.java)
    }

    fun settings(presenter: BaseActivity) {
        present(presenter = presenter, className = SettingsActivity::class.java, isStarting = false)
    }

    fun stage(presenter: BaseActivity) {
        present(presenter = presenter, className = StageActivity::class.java, isStarting = true)
    }

    fun termsReview(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "review")
        params.put("content", "terms")
        present(presenter = presenter, className = TermsActivity::class.java, isStarting = false, params = params)
    }

    fun termsAccept(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "accept")
        params.put("content", "terms")
        present(presenter = presenter, className = TermsActivity::class.java, isStarting = true, params = params)
    }

    fun privacyReview(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "review")
        params.put("content", "privacy")
        present(presenter = presenter, className = TermsActivity::class.java, isStarting = false, params = params)
    }

    fun privacyAccept(presenter: BaseActivity) {

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