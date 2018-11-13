package com.kimlic

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.kimlic.quorum.Web3
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class Web3Test {

    // Variables

    private var targetContext: Context? = null
    private val url = BuildConfig.QUORUM_URL

    @Before
    fun setupContext() {
        targetContext = getInstrumentation().targetContext
    }

    @Test
    fun instanceNotNull() {
        val web3Instance = Web3.getInstance(url, targetContext!!).web3
        assertNotNull(web3Instance)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun instanceNullContext() {
        targetContext = null
        val web3Instance = Web3.getInstance(URL = url, context = targetContext!!).web3
        assertNotNull(web3Instance)
    }

    @After
    fun clearContext() {
        targetContext = null
    }
}