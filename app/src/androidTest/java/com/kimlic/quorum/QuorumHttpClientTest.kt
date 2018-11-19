package com.kimlic.quorum

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.kimlic.BuildConfig
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class QuorumHttpClientTest {

    // Variables

    private var targetContext: Context? = null
    private val url = BuildConfig.QUORUM_URL

    @Before
    fun setupContext() {
        targetContext = getInstrumentation().targetContext
    }

    @Test
    fun instanceNotNull() {
        val quorumHttpClientInstance = QuorumHttpClient(context = targetContext!!, url = url)
        assertNotNull(quorumHttpClientInstance)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun quorumHttpClientInstanceNullContext() {
        targetContext = null
        val quorumHttpClientInstance = QuorumHttpClient(context = targetContext!!, url = url)
        assertNotNull(quorumHttpClientInstance)
    }

    @After
    fun clearContext() {
        targetContext = null
    }
}