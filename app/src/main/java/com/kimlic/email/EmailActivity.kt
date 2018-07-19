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
import com.kimlic.db.KimlicDB
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_email.*
import org.json.JSONObject
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.util.concurrent.ExecutionException

class EmailActivity : BaseActivity() {

    // Variables

    private lateinit var blockchainUpdatingFragment: BlockchainUpdatingFragment

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
        nextBt.setOnClickListener {
            // Stub
            if (isEmailValid()) {
                val email = emailEt.text.toString()
                PresentationManager.emailVerify(this, email)
            }
            //manageInput()
        }
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
//            }).start()
                // Stub
            })
            PresentationManager.emailVerify(this@EmailActivity, emailEt.text.toString())
        } else {
            emailEt.setError("invalid")
        }
    }

    private fun showProgress() {
        blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()

        object : CountDownTimer(1000, 1000) {
            override fun onFinish() {
                blockchainUpdatingFragment.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread { blockchainUpdatingFragment.dismiss() }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()

    private fun unableToProceed() {
        runOnUiThread { hideProgress() }
        nextBt.isClickable = true
        showPopup(message = "Unable to proceed vith verification")
    }
}