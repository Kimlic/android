package com.kimlic.quorum

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.kimlic.API.KimlicApi
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.BuildConfig
import com.kimlic.quorum.crypto.Sha
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.web3j.utils.Convert

@RunWith(AndroidJUnit4::class)
@LargeTest
class QuorumKimlicTest {

    // Variables

    companion object {
        private const val MNEMONIC_PHRASES_QUANTITY = 12
        private const val MNEMONIC_GENERATED = "kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic"
        private const val MNEMONIC_25_KIMTOKENS = "tobacco world truly mushroom bike basic pioneer nurse please bar enrich fatal"
        private const val PHONE_NUMBER = "+38050505050"
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

    /*
    * Try to get accountStorageAdapter without setKimlicContractsContextAddress(address: String)
    * */
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

    /*
    * Try to get accountStorageAdapter with setting ContractsContextAddress
    * */

    @Test
    fun getAccountStorageAdapter_ContractAddressPresent() {
        val quorumInstance = QuorumKimlic.createInstance(null, targetContext)
        walletAddress = quorumInstance.walletAddress

        configRequest(walletAddress = walletAddress!!,
                onSuccess = { accountStorageAdapter ->
                    quorumInstance.setAccountStorageAdapterAddress(accountStorageAdapter)
                },
                onError = { assertTrue(false) })
    }

    @Test(expected = InterruptedException::class)
    fun setFieldMainData_NoAccountStorageAdapter() {
        val quorumInstance = QuorumKimlic.createInstance(null, targetContext)

        quorumInstance.setFieldMainData(Sha.sha256(PHONE_NUMBER), "phone")
    }

    @Test
    fun setMainFieldData_AccountStorageAdapterPresent() {
        val quorumInstance = QuorumKimlic.createInstance(null, targetContext)
        walletAddress = quorumInstance.walletAddress
        configRequest(walletAddress = walletAddress!!,
                onSuccess = { contextContractAddress ->
                    quorumInstance.setAccountStorageAdapterAddress(contextContractAddress)
                    val transactionReceipt = quorumInstance.setFieldMainData("phone", PHONE_NUMBER)
                    assertNotNull(transactionReceipt)
                    assertNotNull(transactionReceipt.transactionHash)
                },
                onError = {
                    assertTrue(false)
                })
    }

    @Test(expected = KotlinNullPointerException::class)
    fun getKimlicTokenContractAddress_emptyContextsContract() {
        val quorumInstance = QuorumKimlic.createInstance(null, targetContext)
        val kimlicTokenContractAddress = quorumInstance.kimlicTokenAddress
    }

    fun getKimlicTokenContractAddress_AccountStorageAdapterPresent() {
        val quorumInstance = QuorumKimlic.createInstance(MNEMONIC_25_KIMTOKENS, targetContext)
        walletAddress = quorumInstance.walletAddress

        configRequest(walletAddress!!,
                onSuccess = { contextContractAddress ->
                    quorumInstance.setAccountStorageAdapterAddress(contextContractAddress)

                    val kimlicTokenContractAddress = quorumInstance.kimlicTokenAddress
                    quorumInstance.setKimlicToken(kimlicTokenContractAddress)
                    val wei = quorumInstance.getTokenBalance(walletAddress!!)
                    val token = Convert.fromWei(wei.toString(), Convert.Unit.ETHER)

                    assertEquals(token.toInt(), 25)
                },
                onError = {
                    throw Exception("Quorun context_contract address error")
                })
    }

    @After
    fun clearResources() {
        QuorumKimlic.destroyInstance()
        walletAddress = null
    }

    // Private helpers

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