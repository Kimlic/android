package com.kimlic

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.kimlic.quorum.EthereumAddressValidator
import com.kimlic.quorum.QuorumKimlic
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

    @After
    fun clearResources() {
        QuorumKimlic.destroyInstance()
    }
}