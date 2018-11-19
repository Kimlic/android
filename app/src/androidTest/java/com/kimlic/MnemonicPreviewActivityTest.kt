package com.kimlic

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kimlic.mnemonic.MnemonicPreviewActivity
import com.kimlic.mnemonic.MnemonicPreviewViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)

class MnemonicPreviewActivityTest {

    // Constants

    companion object {
        private const val MNEMONIC = "a s d f g h j k l g h n"
    }

    // Rule

    @Rule
    @JvmField
    var mnemonicActivityPreviewRule = ActivityTestRule<MnemonicPreviewActivity>(MnemonicPreviewActivity::class.java)
    private var mnemonicActivity: MnemonicPreviewActivity? = null

    var model: MnemonicPreviewViewModel = mock(MnemonicPreviewViewModel::class.java)

    @Before
    fun setup() {
//        mnemonicModelMock = mock(MnemonicPreviewViewModel::class.java)
//        Mockito.`when`(mnemonicModelMock!!.userMnemonic()).thenReturn(MNEMONIC)
//        mnemonicActivityPreviewRule.activity.initViewModel(model = mnemonicModelMock)
//        mnemonicActivity = mnemonicActivityPreviewRule.activity

    }

    @Test
    fun activityIsShown() {
        //mnemonicModelMock = mock(MnemonicPreviewViewModel::class.java)
        Mockito.`when`(model.userMnemonic()).thenReturn(MNEMONIC)
        //mnemonicActivityPreviewRule.activity.initViewModel(model = model)
        mnemonicActivity = mnemonicActivityPreviewRule.activity




        Thread.sleep(2000)
        onView(withId(R.id.phraseBt)).perform(click())

        Thread.sleep(10000)
    }

    @After
    fun clear() {
        mnemonicActivity = null
        //mnemonicModelMock = null
    }
}