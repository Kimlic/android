//package com.kimlic
//
//import android.support.test.espresso.Espresso.onView
//import android.support.test.espresso.action.ViewActions.click
//import android.support.test.espresso.matcher.ViewMatchers.*
//import android.support.test.filters.LargeTest
//import android.support.test.rule.ActivityTestRule
//import android.support.test.runner.AndroidJUnit4
//import android.view.View
//import android.view.ViewGroup
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.hamcrest.Matchers.allOf
//import org.hamcrest.TypeSafeMatcher
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@LargeTest
//@RunWith(AndroidJUnit4::class)
//class PasscodeUnlockAcivityTest {
//
//    @Rule
//    @JvmField
//    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)
//
//    @Test
//    fun passcodeUnlockAcivityTest() {
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        Thread.sleep(7000)
//
//        val appCompatButton = onView(
//                allOf(withId(R.id.passcodeBt1), withText("1"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.linearLayout),
//                                        1),
//                                0),
//                        isDisplayed()))
//        appCompatButton.perform(click())
//
//        val appCompatButton2 = onView(
//                allOf(withId(R.id.passcodeBt2), withText("2"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.linearLayout),
//                                        1),
//                                1),
//                        isDisplayed()))
//        appCompatButton2.perform(click())
//
//        val appCompatButton3 = onView(
//                allOf(withId(R.id.passcodeBt3), withText("3"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.linearLayout),
//                                        1),
//                                2),
//                        isDisplayed()))
//        appCompatButton3.perform(click())
//
//        val appCompatButton4 = onView(
//                allOf(withId(R.id.passcodeBt6), withText("6"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.linearLayout),
//                                        1),
//                                5),
//                        isDisplayed()))
//        appCompatButton4.perform(click())
//    }
//
//    private fun childAtPosition(
//            parentMatcher: Matcher<View>, position: Int): Matcher<View> {
//
//        return object : TypeSafeMatcher<View>() {
//            override fun describeTo(description: Description) {
//                description.appendText("Child at position $position in parent ")
//                parentMatcher.describeTo(description)
//            }
//
//            public override fun matchesSafely(view: View): Boolean {
//                val parent = view.parent
//                return parent is ViewGroup && parentMatcher.matches(parent)
//                        && view == parent.getChildAt(position)
//            }
//        }
//    }
//}
