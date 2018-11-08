package com.kimlic

import com.kimlic.db.KimlicDB
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit


class MockTest {


    @Mock
    lateinit var database: KimlicDB

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule() // Tells Mokito create mocks on the @Mock annotation


    @Test
    fun test() {
        MockitoAnnotations.initMocks(this)
        //print("Hello")
        //TestPrint.printHelloWorld()

        //testPrint.printHelloWordldFromCreatedObject()

        //val testPrint = mock(TestPrint::class.java) // Posible to create mocks by mock Annotation
//        assertNotNull(testPrint)
//        print(testPrint)
//
//        testPrint.printHelloWordldFromCreatedObject()
    }
}