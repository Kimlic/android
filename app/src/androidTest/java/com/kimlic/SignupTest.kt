//package com.kimlic
//
//
//import android.support.test.espresso.Espresso.onView
//import android.support.test.espresso.action.ViewActions.*
//import android.support.test.espresso.matcher.ViewMatchers.*
//import android.support.test.filters.LargeTest
//import android.support.test.rule.ActivityTestRule
//import android.support.test.runner.AndroidJUnit4
//import android.view.View
//import android.view.ViewGroup
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.hamcrest.Matchers.`is`
//import org.hamcrest.Matchers.allOf
//import org.hamcrest.TypeSafeMatcher
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@LargeTest
//@RunWith(AndroidJUnit4::class)
//class SignupTest {
//
//    @Rule
//    @JvmField
//    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)
//
//    @Test
//    fun signupTest() {
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        Thread.sleep(7000)
//
//        val appCompatButton = onView(allOf(withId(R.id.createBt), withText("Create new identity"), childAtPosition(childAtPosition(withId(android.R.id.content),0), 2),
//                isDisplayed()))
//        appCompatButton.perform(click())
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        Thread.sleep(7000)
//
//        val appCompatButton2 = onView(
//                allOf(withId(R.id.confirmBt), withText("Accept"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                3),
//                        isDisplayed()))
//        appCompatButton2.perform(click())
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        Thread.sleep(7000)
//
//        val appCompatButton3 = onView(
//                allOf(withId(R.id.confirmBt), withText("Accept"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                3),
//                        isDisplayed()))
//        appCompatButton3.perform(click())
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        Thread.sleep(7000)
//
//        val appCompatEditText = onView(
//                allOf(withId(R.id.phoneEt), withText("+"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
//                                        0),
//                                0),
//                        isDisplayed()))
//        appCompatEditText.perform(click())
//
//        val appCompatEditText2 = onView(
//                allOf(withId(R.id.phoneEt), withText("+"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
//                                        0),
//                                0),
//                        isDisplayed()))
//        appCompatEditText2.perform(replaceText("+380 50 866 8370"))
//
//        val appCompatEditText3 = onView(
//                allOf(withId(R.id.phoneEt), withText("+380 50 866 8370"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
//                                        0),
//                                0),
//                        isDisplayed()))
//        appCompatEditText3.perform(closeSoftKeyboard())
//
//        val appCompatEditText4 = onView(
//                allOf(withId(R.id.phoneEt), withText("+380 50 866 8370"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
//                                        0),
//                                0),
//                        isDisplayed()))
//        appCompatEditText4.perform(pressImeActionButton())
//
//        val appCompatButton4 = onView(
//                allOf(withId(R.id.nextBt), withText("Next"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                4),
//                        isDisplayed()))
//        appCompatButton4.perform(click())
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        Thread.sleep(7000)
//
//        val appCompatEditText5 = onView(
//                allOf(withId(R.id.digit1Et),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                0),
//                        isDisplayed()))
//        appCompatEditText5.perform(click())
//
//        val appCompatEditText6 = onView(
//                allOf(withId(R.id.digit1Et),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                0),
//                        isDisplayed()))
//        appCompatEditText6.perform(replaceText("94"), closeSoftKeyboard())
//
//        val appCompatEditText7 = onView(
//                allOf(withId(R.id.digit1Et), withText("9"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                0),
//                        isDisplayed()))
//        appCompatEditText7.perform(replaceText("49"))
//
//        val appCompatEditText8 = onView(
//                allOf(withId(R.id.digit1Et), withText("49"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                0),
//                        isDisplayed()))
//        appCompatEditText8.perform(closeSoftKeyboard())
//
//        val appCompatEditText9 = onView(
//                allOf(withId(R.id.digit2Et),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                1),
//                        isDisplayed()))
//        appCompatEditText9.perform(replaceText("9"), closeSoftKeyboard())
//
//        val appCompatEditText10 = onView(
//                allOf(withId(R.id.digit3Et),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                2),
//                        isDisplayed()))
//        appCompatEditText10.perform(replaceText("2"), closeSoftKeyboard())
//
//        val appCompatEditText11 = onView(
//                allOf(withId(R.id.digit4Et),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                3),
//                        isDisplayed()))
//        appCompatEditText11.perform(replaceText("7"), closeSoftKeyboard())
//
//        val appCompatEditText12 = onView(
//                allOf(withId(R.id.digit4Et), withText("7"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(`is`("android.widget.LinearLayout")),
//                                        2),
//                                3),
//                        isDisplayed()))
//        appCompatEditText12.perform(pressImeActionButton())
//
//        val appCompatButton5 = onView(
//                allOf(withId(R.id.verifyBt), withText("Verify number"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                4),
//                        isDisplayed()))
//        appCompatButton5.perform(click())
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
