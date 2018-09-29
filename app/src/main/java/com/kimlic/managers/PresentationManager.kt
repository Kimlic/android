package com.kimlic.managers

import android.app.Activity
import android.content.Intent
import com.kimlic.BaseActivity
import com.kimlic.account.AccountActivity
import com.kimlic.address.AddressActivity
import com.kimlic.auth.TouchIdActivity
import com.kimlic.documents.DocumentChoiceActivity
import com.kimlic.documents.DocumentDetails
import com.kimlic.documents.DocumentVerifyActivity
import com.kimlic.documents.PortraitActivity
import com.kimlic.email.EmailActivity
import com.kimlic.email.EmailVerifyActivity
import com.kimlic.mnemonic.MnemonicPreviewActivity
import com.kimlic.mnemonic.MnemonicVerifyActivity
import com.kimlic.name.NameActivity
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.phone.PhoneActivity
import com.kimlic.phone.PhoneVerifyActivity
import com.kimlic.recovery.AccountRecoveryActivity
import com.kimlic.recovery.SignUpRecoveryActivity
import com.kimlic.settings.SettingsActivity
import com.kimlic.stage.CompanyDetails
import com.kimlic.stage.StageActivity
import com.kimlic.terms.TermsActivity
import com.kimlic.tutorial.TutorialActivity
import com.kimlic.utils.AppConstants

object PresentationManager {

    // Public

    fun tutorials(presenter: BaseActivity) {
        present(presenter = presenter, className = TutorialActivity::class.java, isStarting = false)
    }

    // Passcode

    fun passcode(presenter: BaseActivity, requestCode: Int) {
        val params = HashMap<String, String>()
        params["action"] = "set"
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = false, params = params, requestCode = requestCode)
    }

    fun passcodeUnlock(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "unlock"
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = true, params = params)
    }

    fun passcodeDisable(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "disable"
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = false, params = params)
    }

    fun passcodeChange(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "change"
        present(presenter = presenter, className = PasscodeActivity::class.java, isStarting = false, params = params)
    }

    fun login(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "unlock"
        present(presenter = presenter, className = TouchIdActivity::class.java, isStarting = true, params = params)
    }

    // Touch

    fun touchCreate(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "create"
        present(presenter = presenter, className = TouchIdActivity::class.java, isStarting = false, params = params)
    }

    fun touchDisable(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "disable"
        present(presenter = presenter, className = TouchIdActivity::class.java, isStarting = false, params = params)
    }


    fun phraseVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = MnemonicVerifyActivity::class.java)
    }

    // Phone validation

    fun phoneNumber(presenter: BaseActivity) {
        present(presenter = presenter, className = PhoneActivity::class.java, isStarting = false)
    }

    fun phoneNumberVerify(presenter: BaseActivity, phoneNumber: String = "") {
        val params = HashMap<String, String>()
        params["phone"] = phoneNumber
        present(presenter = presenter, className = PhoneVerifyActivity::class.java, isStarting = false, params = params)
    }

    // Email validation

    fun email(presenter: BaseActivity) {
        present(presenter = presenter, className = EmailActivity::class.java, isStarting = false)
    }

    fun emailVerify(presenter: BaseActivity, email: String = "", requestCode: Int) {
        val params = HashMap<String, String>()
        params["email"] = email
        present(presenter = presenter, className = EmailVerifyActivity::class.java, isStarting = false, params = params, requestCode = requestCode)
    }

    // Name

    fun name(presenter: BaseActivity) {
        present(presenter = presenter, className = NameActivity::class.java, isStarting = false)
    }

    // Recovery

    fun signUpRecovery(presenter: BaseActivity) {
        present(presenter = presenter, className = SignUpRecoveryActivity::class.java, isStarting = true)
    }

    fun recovery(presenter: BaseActivity) {
        present(presenter = presenter, className = AccountRecoveryActivity::class.java, isStarting = false)
    }

    fun recoveryEnable(presenter: BaseActivity) {
        present(presenter = presenter, className = MnemonicPreviewActivity::class.java, isStarting = false)
    }

    fun passphraseCreate(presenter: BaseActivity) {
        present(presenter = presenter, className = MnemonicPreviewActivity::class.java)
    }

    // Verification

    fun documentChoiseVerify(presenter: BaseActivity) {
        present(presenter = presenter, className = DocumentChoiceActivity::class.java, isStarting = false)
    }

    fun verifyDocument(presenter: BaseActivity, documentType: String, country: String, requestCode: Int) {
        val params = HashMap<String, String>()
        params[AppConstants.DOCUMENT_TYPE.key] = documentType
        params[AppConstants.COUNTRY.key] = country
        present(presenter = presenter, className = DocumentVerifyActivity::class.java, isStarting = false, params = params, requestCode = requestCode)
    }

    fun detailsDocument(presenter: BaseActivity, documentType: String) {
        val params = HashMap<String, String>()
        params[AppConstants.DOCUMENT_TYPE.key] = documentType
        present(presenter = presenter, className = DocumentDetails::class.java, isStarting = false, params = params)
    }

    fun detailsDocumentChoose(presenter: BaseActivity, documentType: String, requestCode: Int) {
        val params = HashMap<String, String>()
        params[AppConstants.DOCUMENT_TYPE.key] = documentType
        params["action"] = "choose"
        present(presenter = presenter, className = DocumentDetails::class.java, isStarting = false, params = params, requestCode = requestCode)
    }

    fun detailsDocumentSave(presenter: BaseActivity, documentType: String, country: String, requestCode: Int) {
        val params = HashMap<String, String>()
        params[AppConstants.DOCUMENT_TYPE.key] = documentType
        params["action"] = "previewAndSave"
        params["country"] = country
        present(presenter = presenter, className = DocumentDetails::class.java, isStarting = false, params = params, requestCode = requestCode)
    }

    // Settings

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
        params["action"] = "review"
        params["content"] = AppConstants.TERMS.key
        present(presenter = presenter, className = TermsActivity::class.java, isStarting = false, params = params)
    }

    fun termsAccept(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "accept"
        params["content"] = AppConstants.TERMS.key
        present(presenter = presenter, className = TermsActivity::class.java, isStarting = true, params = params)
    }

    fun privacyReview(presenter: BaseActivity) {
        val params = HashMap<String, String>()
        params["action"] = "review"
        params["content"] = AppConstants.PRIVACY.key
        present(presenter = presenter, className = TermsActivity::class.java, isStarting = false, params = params)
    }

    fun about(presenter: BaseActivity) {
        presenter.showToast("About activity")
    }

    fun address(presenter: BaseActivity) {
        present(presenter = presenter, className = AddressActivity::class.java, isStarting = false)
    }

    fun account(presenter: BaseActivity, url: String) {
        val params = HashMap<String, String>()
        params["path"] = url
        present(presenter = presenter, className = AccountActivity::class.java, params = params, isStarting = false)
    }

    fun companyDetails(presenter: BaseActivity, companyId: String) {
        val params = HashMap<String, String>()
        params["companyId"] = companyId
        present(presenter = presenter, className = CompanyDetails::class.java, params = params)
    }

    // Private

    private fun present(presenter: BaseActivity, className: Class<out Activity>, isStarting: Boolean = false, params: HashMap<String, String>? = null, requestCode: Int? = null) {
        val intent = Intent(presenter, className)
        params?.let { it.forEach { (key, value) -> intent.putExtra(key, value) } }

        if (isStarting) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("starting", true)
            presenter.finish()
        }

        if (requestCode == null)
            presenter.startActivity(intent) else presenter.startActivityForResult(intent, requestCode)
    }
}