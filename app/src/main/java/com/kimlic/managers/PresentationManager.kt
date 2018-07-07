package com.kimlic.managers

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.SignupRecoveryActivity
import com.kimlic.address.AddressActivity
import com.kimlic.auth.TouchIdActivity
import com.kimlic.validation.PortraitActivity
import com.kimlic.email.EmailActivity
import com.kimlic.email.EmailVerifyActivity
import com.kimlic.name.NameActivity
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.phone.PhoneActivity
import com.kimlic.phone.PhoneVerifyActivity
import com.kimlic.phrase.PhraseGenerateActivity
import com.kimlic.phrase.PhraseVerifyActivity
import com.kimlic.recovery.AccountRecoveryActivity
import com.kimlic.settings.SettingsActivity
import com.kimlic.splash.SplashScreenActivity
import com.kimlic.stage.StageActivity
import com.kimlic.terms.TermsActivity
import com.kimlic.tutorial.TutorialActivity
import com.kimlic.validation.DocumentVerifyActivity
import com.kimlic.validation.DocumentVerifyChooseActivity
import com.kimlic.validation.PassportVerifyActivity
import java.util.*

object PresentationManager {

    private val TAG = this.javaClass.simpleName

    fun splash(presentrer: BaseActivity) {
        present(presenter = presentrer, className = SplashScreenActivity::class.java, isStarting = true)
    }

    fun tutorials(presenter: BaseActivity) {
        present(presenter = presenter, className = TutorialActivity::class.java, isStarting = false)
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

    fun login(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "unlock")
        present(presenter = presenter, className = TouchIdActivity::class.java, isStarting = true, params = params)
        //present(presenter = presenter, className = LoginActivity::class.java, isStarting = true)
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

    fun phoneNumber(presenter: BaseActivity) {
        present(presenter = presenter, className = PhoneActivity::class.java, isStarting = false)
    }

    fun phoneNumberVerify(presenter: BaseActivity, phoneNumber: String = "") {
        val params = HashMap<String, String>()
        params.put("phone", phoneNumber)
        present(presenter = presenter, className = PhoneVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun name(presenter: BaseActivity) {
        present(presenter = presenter, className = NameActivity::class.java, isStarting = false)
    }

    // TODO check this flow
    fun email(presenter: BaseActivity) {
        present(presenter = presenter, className = EmailActivity::class.java, isStarting = false)
    }

    // TODO check this flow
    fun emailVerify(presenter: BaseActivity, email: String = "") {
        val params = HashMap<String, String>()
        params.put("email", email)
        present(presenter = presenter, className = EmailVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun recovery(presenter: BaseActivity) {
        present(presenter = presenter, className = AccountRecoveryActivity::class.java, isStarting = false)
    }

    fun documentChooseVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = DocumentVerifyChooseActivity::class.java, isStarting = false)
    }

    fun documentVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false)
    }

    fun signupRecovery(presenter: BaseActivity) {
        present(presenter = presenter, className = SignupRecoveryActivity::class.java, isStarting = true)
    }

    fun passhraseCreate(presenter: BaseActivity) {
        present(presenter = presenter, className = PhraseGenerateActivity::class.java)
    }

    fun settings(presenter: BaseActivity) {
        present(presenter = presenter, className = SettingsActivity::class.java, isStarting = false)
    }

    fun stage(presenter: BaseActivity) {
        present(presenter = presenter, className = StageActivity::class.java, isStarting = true)
    }

    fun portraitPhoto(presenter: BaseActivity) {
        present(presenter = presenter, className = PortraitActivity::class.java, isStarting = false)
    }

    fun passportVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = PassportVerifyActivity::class.java, isStarting = false)
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

    fun about(presenter: BaseActivity) {
        //present(presenter = presenter, className = ...)
        presenter.showToast("About activity")
    }

    fun address(presenter: BaseActivity) {
        present(presenter = presenter, className = AddressActivity::class.java, isStarting = false)
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