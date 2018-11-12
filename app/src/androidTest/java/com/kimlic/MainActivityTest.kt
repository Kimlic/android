package com.kimlic

import android.support.constraint.ConstraintLayout
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@RunWith(AndroidJUnit4::class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {

    // Rule

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    private var activity: MainActivity? = null

    @Before
    fun setup() {
        activity = activityTestRule.activity
    }

    @Test
    fun testLaunch() {
        val root = activity?.findViewById<ConstraintLayout>(R.id.root)
        Assert.assertNotNull(root)
    }

    @After
    fun tearDown() {
        activity = null
    }
}