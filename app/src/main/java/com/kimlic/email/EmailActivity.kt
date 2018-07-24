package com.kimlic.email

import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_email.*
import org.json.JSONObject
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.util.concurrent.ExecutionException

class EmailActivity : BaseActivity() {

    // Variables

    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null
    private var timer: CountDownTimer? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(emailEt)
    }

    // Private

    private fun setupUI() {
        nextBt.setOnClickListener { manageInput() }

        emailEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        backTv.setOnClickListener { finish() }
        backBt.setOnClickListener { finish() }
    }

    private fun manageInput() {
        if (isEmailValid()) {
            showProgress()
            nextBt.isClickable = false
            emailEt.setError(null)

            val email = emailEt.text.toString()

            Thread(object : Runnable {
                override fun run() {
                    val quorumKimlic = QuorumKimlic.getInstance()
                    var receiptEmail: TransactionReceipt? = null
                    try {
                        receiptEmail = quorumKimlic.setAccountFieldMainData(Sha.sha256(email), "email")
                    } catch (e: ExecutionException) {
                        unableToProceed()
                    } catch (e: InterruptedException) {
                        unableToProceed()
                    }

                    if (receiptEmail != null && receiptEmail.transactionHash.isNotEmpty()) {
                        val params = emptyMap<String, String>().toMutableMap(); params.put("email", email)
                        val headers = emptyMap<String, String>().toMutableMap(); headers.put("account-address", quorumKimlic.walletAddress)

                        val request = KimlicRequest(Request.Method.POST, QuorumURL.emailVerify.url, headers, params, Response.Listener { response ->
                            hideProgress()
                            val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()

                            if (responceCode.startsWith("2")) {
                                nextBt.isClickable = true
                                PresentationManager.emailVerify(this@EmailActivity, emailEt.text.toString())
                            } else unableToProceed()
                        },
                                Response.ErrorListener { unableToProceed() }
                        )
                        VolleySingleton.getInstance(this@EmailActivity).addToRequestQueue(request)
                    } else unableToProceed()
                }
            }).start()
        } else {
            emailEt.setError("invalid")
        }
    }

    private fun showProgress() {
        timer = object : CountDownTimer(500, 500) {
            override fun onFinish() {
                blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
                blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread { if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer.let { it?.cancel() } }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()

    private fun unableToProceed() {
        hideProgress()
        runOnUiThread { nextBt.isClickable = true; showPopup(message = "Unable to proceed with verification") }
    }
}