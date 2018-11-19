package com.kimlic

import android.os.Build
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kimlic.recovery.SignUpRecoveryActivity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpRecoveryActivityUITest {

    // Rule

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<SignUpRecoveryActivity>(SignUpRecoveryActivity::class.java)

    // Variable

    private var signUpRecoveryActivity: SignUpRecoveryActivity? = null

    @Before
    fun setup() {
        signUpRecoveryActivity = activityRule.activity
    }

    @Test
    fun activityIsDisplayed() {
        sleep()
        onView(withId(R.id.logoIv)).check(matches(isDisplayed()))

        signUpRecoveryActivity?.finish()

        sleep()
        checkIfFinished()
    }

    @Test
    fun clickCreateNewIdentity() {
        sleep()
        onView(withId(R.id.createBt)).check(matches(isDisplayed())).perform(click())
        sleep()
        onView(withId(R.id.pagerVp)).check(matches(isDisplayed()))

        signUpRecoveryActivity?.finish()
        sleep()
        checkIfFinished()
    }

    @Test
    fun clickRecoverMyIdentity() {
        sleep()
        onView(withId(R.id.recoverBt)).check(matches(isDisplayed())).perform(click())
        sleep()
        onView(withId(R.id.webView)).check(matches(isDisplayed()))

        signUpRecoveryActivity?.finish()
        sleep(2000)
        checkIfFinished()
    }

    @After
    fun clear() {
        signUpRecoveryActivity = null
    }

    // Private helpers

    private fun checkIfFinished() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            assertTrue(activityRule.activity.isDestroyed)
        }
    }

    private fun sleep(millis: Long = 1000) {
        Thread.sleep(millis)
    }
}