package com.kimlic

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicApi
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.quorum.QuorumKimlic
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/*
* This test creates QuorumInstance. Then generates mnemonic and creates wallet address.
* */

@RunWith(AndroidJUnit4::class)
class CreateNewUserTest {

    // Variables

    private var targetContext: Context? = null
    private var mnemonic: String? = null
    private var walletAddress: String? = null
    private lateinit var quorumKimlic: QuorumKimlic
    private var contextContract: String? = null


    @Before
    fun setupContext() {
        targetContext = getInstrumentation().targetContext
    }

    @Test
    fun createNewUserTest() {
        quorumKimlic = QuorumKimlic.createInstance(mnemonic_ = null, context = targetContext)
        Assert.assertNotNull(quorumKimlic)

        mnemonic = quorumKimlic.mnemonic
        assertTrue(mnemonicContainsTwelveWords(mnemonic.orEmpty()))

        walletAddress = quorumKimlic.walletAddress
        assertNotNull(walletAddress)

        initNewUserRegistration(walletAddress!!,
                onSuccess = { contextContract_ ->
                    contextContract = contextContract_
                    assertNotNull(contextContract)
                },
                onError = {
                    contextContract = null
                    assertNotNull(contextContract)
                })
    }

    @After
    fun clearResources() {
        QuorumKimlic.destroyInstance()
    }

    // Private

    private fun mnemonicContainsTwelveWords(mnemonic: String): Boolean = mnemonic.split(" ").size == 12

    private fun initNewUserRegistration(walletAddress: String, onSuccess: (contextContract: String) -> Unit, onError: () -> Unit) {
        val url = BuildConfig.API_CORE_URL + KimlicApi.CONFIG.path

        val headers = mapOf(Pair("account-address", walletAddress))
        val addressRequest = KimlicJSONRequest(Request.Method.GET, url, headers, JSONObject(),
                Response.Listener {
                    if (!it.getJSONObject("headers").optString("statusCode").toString().startsWith("2")) {
                        onError()
                        return@Listener
                    }
                    val contextContractAddress = it.getJSONObject("data").optString("context_contract")
                    onSuccess(contextContractAddress)
                    Log.d("INSTRUMENTED", "contextContractAddress  - $contextContractAddress")
                },
                Response.ErrorListener { onError() })
        VolleySingleton.getInstance(targetContext!!).addToRequestQueue(addressRequest)
    }
}