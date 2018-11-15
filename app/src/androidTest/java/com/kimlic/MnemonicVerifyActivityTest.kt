package com.kimlic

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kimlic.mnemonic.MnemonicVerifyActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MnemonicVerifyActivityTest {

    @Rule
    @JvmField
    val mnemonicRule = ActivityTestRule<MnemonicVerifyActivity>(MnemonicVerifyActivity::class.java)

    private var mnemonicActivity: MnemonicVerifyActivity? = null

    @Before
    fun setup() {
        mnemonicActivity = mnemonicRule.activity
        mnemonicActivity.apply {
        }
    }

    @Test
    fun test() {
        onView(withId(R.id.phrase1Et)).check(matches(isDisplayed()))
    }

    @After
    fun after() {
        mnemonicActivity?.finish()
    }
}