package com.kimlic


import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import com.kimlic.quorum.QuorumHttpClient
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.Web3
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuorumCompleteTest {

    // Variables

    private var targetContext: Context? = null
    private val url = BuildConfig.QUORUM_URL

    @Before
    fun setupContext() {
        targetContext = getInstrumentation().targetContext
    }

    @Test
    fun web3InstanceTest() {
        val web3 = Web3.getInstance(url, targetContext!!)
        assertNotNull(web3)
    }

    @Test
    fun quorumHttpInstanceTest() {
        val quorumHttpClientInstance = QuorumHttpClient(context = targetContext!!, url = url)
        assertNotNull(quorumHttpClientInstance)
    }

    @Test
    fun quorumKimlicInstanceTest() {
        val quorumKimlicInstance = QuorumKimlic.createInstance(null, context = targetContext)
        assertNotNull(quorumKimlicInstance)
        QuorumKimlic.destroyInstance()
    }
}