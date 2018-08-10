package com.kimlic.managers

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.recovery.SignupRecoveryActivity
import com.kimlic.address.AddressActivity
import com.kimlic.auth.TouchIdActivity
import com.kimlic.documents.*
import com.kimlic.email.EmailActivity
import com.kimlic.email.EmailVerifyActivity
import com.kimlic.mnemonic.MnemonicPreviewActivity
import com.kimlic.mnemonic.MnemonicVerifyActivity
import com.kimlic.name.NameActivity
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.phone.PhoneActivity
import com.kimlic.phone.PhoneVerifyActivity
import com.kimlic.recovery.AccountRecoveryActivity
import com.kimlic.settings.SettingsActivity
import com.kimlic.splash.SplashScreenActivity
import com.kimlic.stage.StageActivity
import com.kimlic.terms.TermsActivity
import com.kimlic.tutorial.TutorialActivity
import com.kimlic.utils.AppConstants
import com.kimlic.utils.UserPhotos
import com.kimlic.vendors.VendorsActivity

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
        present(presenter = presenter, className = MnemonicPreviewActivity::class.java, isStarting = false)
    }

    fun phraseVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = MnemonicVerifyActivity::class.java)
    }

    // TODO probably will be changed
    fun recoveryDisable(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params.put("action", "disable")
        params.put("content", "terms")
        present(presenter = presenter, className = MnemonicVerifyActivity::class.java, params = params)
    }

    // Phone validation

    fun phoneNumber(presenter: BaseActivity) {
        present(presenter = presenter, className = PhoneActivity::class.java, isStarting = false)
    }

    fun phoneNumberVerify(presenter: BaseActivity, phoneNumber: String = "") {
        val params = HashMap<String, String>()
        params.put("phone", phoneNumber)
        present(presenter = presenter, className = PhoneVerifyActivity::class.java, isStarting = false, params = params)
    }

    // Email validation

    fun email(presenter: BaseActivity) {
        present(presenter = presenter, className = EmailActivity::class.java, isStarting = false)
    }

    fun emailVerify(presenter: BaseActivity, email: String = "") {
        val params = HashMap<String, String>()
        params.put("email", email)
        present(presenter = presenter, className = EmailVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun name(presenter: BaseActivity) {
        present(presenter = presenter, className = NameActivity::class.java, isStarting = false)
    }

    fun recovery(presenter: BaseActivity) {
        present(presenter = presenter, className = AccountRecoveryActivity::class.java, isStarting = false)
    }

    fun documentChooseVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = DocumentVerifyChooseActivity::class.java, isStarting = false)
    }

    // Verification

    fun verifyPassport(presenter: BaseActivity, userId: Long) {
        val params = HashMap<String, String>()

        params.put(AppConstants.documentType.key, AppConstants.documentPassport.key)
        params.put(UserPhotos.portraitFilePath.fileName, UserPhotos.passportPortrait.fileName)
        params.put(UserPhotos.frontFilePath.fileName, UserPhotos.passportFront.fileName)
        params.put(UserPhotos.backFilePath.fileName, UserPhotos.passportBack.fileName)

        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun verifyDriverLicence(presenter: BaseActivity, userId: Long) {
        val params = HashMap<String, String>()

        params.put(AppConstants.documentType.key, AppConstants.documentLicense.key)
        params.put(UserPhotos.portraitFilePath.fileName, UserPhotos.licensePortrait.fileName)
        params.put(UserPhotos.frontFilePath.fileName, UserPhotos.licensFront.fileName)
        params.put(UserPhotos.backFilePath.fileName, UserPhotos.licensBack.fileName)

        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun verifyIDCard(presenter: BaseActivity, userId: Long) {
        val params = HashMap<String, String>()

        params.put(AppConstants.documentType.key, AppConstants.documentID.key)
        params.put(UserPhotos.portraitFilePath.fileName, UserPhotos.idPortrait.fileName)
        params.put(UserPhotos.frontFilePath.fileName, UserPhotos.idFront.fileName)
        params.put(UserPhotos.backFilePath.fileName, UserPhotos.idBack.fileName)

        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun verifyPermit(presenter: BaseActivity, userId: Long) {
        val params = HashMap<String, String>()

        params.put(AppConstants.documentType.key, AppConstants.documentPermit.key)
        params.put(UserPhotos.portraitFilePath.fileName, UserPhotos.permitFront.fileName)
        params.put(UserPhotos.frontFilePath.fileName, UserPhotos.permitFront.fileName)
        params.put(UserPhotos.backFilePath.fileName, UserPhotos.permitBack.fileName)

        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false, params = params)
    }

    fun verifyDetails(presenter: BaseActivity, accountAddres: String, documentType: String) {
        val params = HashMap<String, String>()
        params.put(AppConstants.documentType.key, documentType)
        params.put(AppConstants.accountAddress.key, accountAddres)

        present(presenter = presenter, className = DocumentDetails::class.java, isStarting = false, params = params)
    }

    fun signupRecovery(presenter: BaseActivity) {
        present(presenter = presenter, className = SignupRecoveryActivity::class.java, isStarting = true)
    }

    fun passhraseCreate(presenter: BaseActivity) {
        present(presenter = presenter, className = MnemonicPreviewActivity::class.java)
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

    fun vendors(presenter: BaseActivity) {


        present(presenter = presenter, className = VendorsActivity::class.java)
    }

    // Private

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