package com.kimlic

import android.support.constraint.ConstraintLayout
import android.support.test.rule.ActivityTestRule
import org.junit.*

//@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    // Rule

    @get:Rule // - works for Kotlin
//    @Rule @JvmField //
    val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    private var activity: MainActivity? = null

    @Before
    fun setup() {
        activity = activityTestRule.activity
    }

    @Test
    fun testLaunch() {
        print("mainActivity test instance test")
        val root = activity?.findViewById<ConstraintLayout>(R.id.root)
        Assert.assertNotNull(root)
    }

    @After
    fun tearDown() {
        activity = null
    }
}