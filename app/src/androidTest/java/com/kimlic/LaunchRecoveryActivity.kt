package com.kimlic

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import com.kimlic.recovery.SignUpRecoveryActivity
import org.junit.*

class LaunchRecoveryActivity {

    @Rule
    @JvmField
    val activity = ActivityTestRule<SignUpRecoveryActivity>(SignUpRecoveryActivity::class.java)

    var signUpRecoveryActivity: SignUpRecoveryActivity? = null

    @Before
    fun setup() {
        signUpRecoveryActivity = activity.activity
    }

    @Test
    fun launchRecoveryActivityOnRecoveryActivityButtonClick() {
        Assert.assertNotNull(signUpRecoveryActivity!!.findViewById(R.id.createBt))
        onView(withId(R.id.recoverBt)).perform(click())// On this click should be start nexrt activity
        onView(withId(R.id.webView)).check(ViewAssertions.matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        signUpRecoveryActivity = null
    }
}