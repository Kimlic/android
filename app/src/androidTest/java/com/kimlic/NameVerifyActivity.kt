package com.kimlic

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import com.kimlic.db.KimlicDB
import com.kimlic.name.NameActivity
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.mockito.Mock

class NameVerifyActivity {

    @Rule
    @JvmField
    var mnemonicVerifyActivity = ActivityTestRule<NameActivity>(NameActivity::class.java)

    @Mock
    var database: KimlicDB? = null

    private var nameActivity: NameActivity? = null

    @Before
    fun setup() {
        nameActivity = mnemonicVerifyActivity.activity
    }

    @Test
    fun testNameActivity() {
        assertNotNull(nameActivity)
        onView(withId(R.id.nameEt)).perform(click()).perform(typeText("Jon"))
        onView(withId(R.id.lastNameEt)).perform(click()).perform(typeText("Smith"))

        onView(withId(R.id.nameEt)).check(matches(withText("Jon")))
        onView(withId(R.id.lastNameEt)).check(matches(withText("Smith")))


    }

    @After
    fun tearDown() {
        nameActivity = null
    }

}