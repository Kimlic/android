package com.kimlic

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.kimlic.utils.allopen.TestOpen
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.kimlic", appContext.packageName)

        val aMock = mock(A::class.java)
        Log.d("MOCK", "final class is mocked!!!")
    }

    @TestOpen
    class A() {
        fun a(): String = "a"
    }
}
