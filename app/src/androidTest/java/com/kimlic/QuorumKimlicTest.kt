package com.kimlic

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.kimlic.API.KimlicApi
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.quorum.EthereumAddressValidator
import com.kimlic.quorum.QuorumKimlic
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class QuorumKimlicTest {

    // Variables

    companion object {
        private const val MNEMONIC_PHRASES_QUANTITY = 12
        private const val MNEMONIC_GENERATED = "kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic"
        private const val INVALID_ETHEREUM_WALLET_ADDRESS_0 = "1x2507711ca1e38525b01096c6d2ac35a815d33e6d"
        private const val INVALID_ETHEREUM_WALLET_ADDRESS_1 = "0x2507711ca138525b01096c6d2ac35a815d33e6d"
    }

    private var targetContext: Context? = null
    private var walletAddress: String? = null

    @Before
    fun setupContext() {
        targetContext = getInstrumentation().targetContext
        QuorumKimlic.destroyInstance()
    }

    @Test
    fun instanceNotNull() {
        val quorumKimlicInstance = QuorumKimlic.createInstance(mnemonic_ = null, context = targetContext)
        assertNotNull(quorumKimlicInstance)
    }

    @Test(expected = IllegalArgumentException::class)
    fun instanceNullContext() {
        targetContext = null
        QuorumKimlic.createInstance(mnemonic_ = null, context = targetContext)
    }

    @Test
    fun generateNewMnemonicPhases() {
        val quorumInstance = QuorumKimlic.createInstance(mnemonic_ = null, context = targetContext)
        assertNotNull(quorumInstance)
        val mnemonicString = quorumInstance.mnemonic
        val mnemonicPhrases = mnemonicString.split(" ")
        assertEquals(mnemonicPhrases.size, MNEMONIC_PHRASES_QUANTITY)
    }

    @Test
    fun createInstanceWithExistingPhrases() {
        val quorumInstance = QuorumKimlic.createInstance(mnemonic_ = MNEMONIC_GENERATED, context = targetContext)
        assertNotNull(quorumInstance)
        val quorumMnemonicPhrases = quorumInstance.mnemonic
        assertEquals(quorumMnemonicPhrases, MNEMONIC_GENERATED)
    }

    @Test
    fun generateWalletAddress() {
        val quorumInstance = QuorumKimlic.createInstance(mnemonic_ = null, context = targetContext)
        walletAddress = quorumInstance.walletAddress
        assertNotNull(walletAddress)
        assertFalse(EthereumAddressValidator.isValidAddress(INVALID_ETHEREUM_WALLET_ADDRESS_0))
        assertFalse(EthereumAddressValidator.isValidAddress(INVALID_ETHEREUM_WALLET_ADDRESS_1))
        assertTrue(EthereumAddressValidator.isValidAddress(walletAddress!!))
    }

    @Test(expected = InterruptedException::class)
    fun getAccountStorageAdapter_emptyContractAddress() {
        val quorumInstance = QuorumKimlic.createInstance(mnemonic_ = null, context = targetContext)
        quorumInstance.accountStorageAdapter
    }

    @Test(expected = Exception::class)
    fun callQuorumCreateInstanceTwice() {
        QuorumKimlic.createInstance(null, targetContext)
        QuorumKimlic.createInstance(null, targetContext)
    }

    @Test
    fun getAccountStorageAdapter_ContractAddressPresent() {
        val url = BuildConfig.API_CORE_URL + KimlicApi.CONFIG.path

        val quorumInstance = QuorumKimlic.createInstance(null, targetContext)
        val walletAddress = quorumInstance.walletAddress

        configRequest(walletAddress = walletAddress, onSuccess = { a -> }, onError = {})

        val headers = mapOf(Pair("account-address", walletAddress))
        val addressRequest = KimlicJSONRequest(GET, url, headers, JSONObject(), Response.Listener {
            if (!it.getJSONObject("headers").optString("statusCode").toString().startsWith("2")) {
                assertTrue(false)
                return@Listener
            }

            val contextContractAddress = it.getJSONObject("data").optString("context_contract")
            quorumInstance.setKimlicContractsContextAddress(contextContractAddress)

            val accountStorageAdapterAddress = quorumInstance.accountStorageAdapter
            quorumInstance.setAccountStorageAdapterAddress(accountStorageAdapterAddress)

        }, Response.ErrorListener {
            assertTrue(false)
        })

        VolleySingleton.getInstance(targetContext!!).addToRequestQueue(addressRequest)
    }

    @After
    fun clearResources() {
        QuorumKimlic.destroyInstance()
    }

    private fun configRequest(walletAddress: String, onSuccess: (contextContractAddress: String) -> Unit, onError: () -> Unit) {
        val url = BuildConfig.API_CORE_URL + KimlicApi.CONFIG.path

        val headers = mapOf(Pair("account-address", walletAddress))
        val addressRequest = KimlicJSONRequest(GET, url, headers, JSONObject(), Response.Listener {
            if (!it.getJSONObject("headers").optString("statusCode").toString().startsWith("2")) {
                assertTrue(false)
                return@Listener
            }
            val contextContractAddress = it.getJSONObject("data").optString("context_contract")
            onSuccess(contextContractAddress)

        }, Response.ErrorListener {
            assertTrue(false)
        })

        VolleySingleton.getInstance(targetContext!!).addToRequestQueue(addressRequest)

    }
}