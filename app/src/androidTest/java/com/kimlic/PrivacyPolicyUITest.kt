package com.kimlic

import android.content.Intent
import android.os.Build
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.webkit.WebView
import com.kimlic.terms.TermsActivity
import com.kimlic.utils.AppConstants
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@SmallTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PrivacyPolicyUITest {

    // Rule

    @Rule
    @JvmField
    val privacyTestRule = object : ActivityTestRule<TermsActivity>(TermsActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val intent = Intent()
            intent.putExtra(AppConstants.CONTENT.key, AppConstants.PRIVACY.key)
            intent.putExtra(AppConstants.ACTION.key, AppConstants.ACCEPT.key)
            return intent
        }
    }

    // Variables

    private var activity: TermsActivity? = null

    @Before()
    fun setup() {
        activity = privacyTestRule.activity
    }

    @Test
    fun testLaunchPreviewScrollTextPressConfirmClose() {
        Thread.sleep(2000)
        val webView = activity?.findViewById<WebView>(R.id.webView)
        Assert.assertNotNull(webView)

        onView(withId(R.id.webView)).perform(ViewActions.swipeUp())

        Thread.sleep(1000)
        onView(withId(R.id.webView)).check(matches(isDisplayed()))

        Thread.sleep(2000)
        onView(withId(R.id.confirmBt)).perform(click())

        Thread.sleep(2000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            assertTrue(privacyTestRule.activity.isDestroyed)
        }
    }

    @Test
    fun testLaunchPressCancelClose() {
        Thread.sleep(2000)
        val webView = activity?.findViewById<WebView>(R.id.webView)

        Assert.assertNotNull(webView)
        onView(withId(R.id.webView)).check(matches(isDisplayed()))

        Thread.sleep(2000)
        onView(withId(R.id.cancelTv)).perform(click())

        Thread.sleep(2000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            assertTrue(privacyTestRule.activity.isDestroyed)
        }
    }

    @After
    fun clear() {
        activity = null
    }
}