package com.kimlic.ui

import android.content.Context
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.RootMatchers.isDialog
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.kimlic.R
import com.kimlic.preferences.Prefs
import com.kimlic.settings.SettingsActivity
import junit.framework.TestCase.assertTrue
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@MediumTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SettingActivityUITest {

    // Rule

    @Rule
    @JvmField
    val settingsRule = ActivityTestRule<SettingsActivity>(SettingsActivity::class.java)

    // Variables

    private var settingsActivity: SettingsActivity? = null
    private var targetContext: Context? = null

    @Before
    fun setup() {
        targetContext = InstrumentationRegistry.getTargetContext()
        settingsActivity = settingsRule.activity
    }

    // Settings screen

    @Test
    fun launchSettingsScreenTest() {
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))

        settingsActivity?.finish()
        Thread.sleep(1000)
        checkIfFinished()
    }

    // Passcode screen

    @Test
    fun launchPasscodeScreen() {
        Thread.sleep(2000)

        getRecyclerPerformClick(position = 0)
        Thread.sleep(1000)
        onView(withId(R.id.titleTs)).check(matches(isDisplayed()))
        onView(withId(R.id.subtitleTs)).check(matches(isDisplayed()))

        settingsActivity?.finish()
        Thread.sleep(2000)
        checkIfFinished()
    }

    // PasscodeDisable

    @Test
    fun launchPasscodeDisable() {
        if (passcodeStep == 1) {
            Thread.sleep(2000)
            getRecyclerPerformClick(1)
            onView(withId(R.id.titleTs)).check(matches(isDisplayed()))

            settingsActivity?.finish()
            Thread.sleep(2000)
            checkIfFinished()
        }
    }

    // Fingerprint activity test

    @Test
    fun launchTouchId() {
        Thread.sleep(2000)

        getRecyclerPerformClick(1 + passcodeStep)

        if (passcodeStep == 1) {
            onView(withId(R.id.image)).check(matches(isDisplayed()))
            settingsActivity?.finish()
            Thread.sleep(2000)
            checkIfFinished()
        } else {
            onView(withId(R.id.titleTs)).check(matches(isDisplayed()))
            settingsActivity?.finish()
            Thread.sleep(2000)
            checkIfFinished()
        }
    }

    //  GoogleDrive popup

    @Test(timeout = 7000)
    fun launchGoogleDrive() {
        Thread.sleep(2000)

        if (Prefs.isDriveActive) { // GoggleDrive is active - should be shown dialog fragment
            getRecyclerPerformClick(2 + passcodeStep)
            onView(withText(targetContext?.getString(R.string.cancel))).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
            Thread.sleep(1000)
        } else {

        }

        settingsActivity?.finish()
        Thread.sleep(2000)
        checkIfFinished()
    }

    // AccountRecovery activity test

    @Test
    fun launchAccountRecoveryTest() {
        Thread.sleep(2000)

        getRecyclerPerformClick(3 + passcodeStep)
        Thread.sleep(1000)
        onView(withId(R.id.phraseBt)).check(matches(isDisplayed()))

        settingsActivity?.finish()
        Thread.sleep(2000)
        checkIfFinished()
    }

    // Terms activity test

    @Test
    fun launchTermsScreen() {
        Thread.sleep(2000)

        getRecyclerPerformClick(4 + passcodeStep)
        onView(withId(R.id.webView)).check(matches(isDisplayed()))
        onView(withId(R.id.confirmBt)).perform(click())

        settingsActivity?.finish()
        Thread.sleep(2000)
        checkIfFinished()
    }

    // Privacy activity test

    @Test
    fun launchPrivacyScreen() {
        Thread.sleep(2000)

        getRecyclerPerformClick(5 + passcodeStep)
        onView(withId(R.id.webView)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTv)).check(matches(isDisplayed()))
        onView(withId(R.id.confirmBt)).perform(click())

        settingsActivity?.finish()
        Thread.sleep(2000)
        checkIfFinished()
    }

    // Popup "Delete from device"

    @Test
    fun clickDeleteFromDevice() {
        Thread.sleep(2000)
        onView(allOf(withId(R.id.deleteBt), withText(R.string.delete_from_device_))).check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.warningIv)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(R.id.cancelBt)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())

        settingsActivity?.finish()
        Thread.sleep(2000)
        checkIfFinished()
    }


    @After
    fun clear() {
        settingsActivity?.finish()
        targetContext = null
    }

    // Private helpers

    private val passcodeStep: Int
        get() = if (Prefs.isPasscodeEnabled) 1 else 0

    private fun checkIfFinished() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            assertTrue(settingsRule.activity.isDestroyed)
        }
    }

    private fun getRecyclerPerformClick(position: Int): ViewInteraction {
        return onView(allOf(
                withId(R.id.recycler),
                childAtPosition(withClassName(`is`("android.support.constraint.ConstraintLayout")), 1)))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}