package com.kimlic

import android.util.Log
import com.kimlic.db.KimlicDB
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit


class MockTest {

//    @Mock
//    lateinit var database: KimlicDB

//    @Rule
//    @JvmField
//    var mockitoRule = MockitoJUnit.rule() // Tells Mokito create mocks on the @Mock annotation


    @Test
    fun test() {

        print("Hello")
        //TestPrint.printHelloWorld()

        //testPrint.printHelloWordldFromCreatedObject()

        val aMock = mock(A::class.java) // Posible to create mocks by mock Annotation
        print("before aMock a = ${aMock.a()}")
        Mockito.`when`(aMock.a()).thenReturn("B")

        Log.d("MOCK", "after aMock a = ${aMock.a()}")

        print("After mock!!  ${aMock.a()}")
//        assertNotNull(testPrint)
//        print(testPrint)
//
//        testPrint.printHelloWordldFromCreatedObject()
    }

    class A() {
        fun a(): String = "A"
    }
}