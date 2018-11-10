package com.kimlic

import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import com.kimlic.BuildConfig.QUORUM_URL
import com.kimlic.quorum.QuorumHttpClient
import com.kimlic.quorum.Web3
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito.mock


import org.mockito.junit.MockitoJUnitRunner

import java.io.ByteArrayInputStream
import java.io.InputStream

@RunWith(MockitoJUnitRunner::class)
class Web3InstanceTest {

    // Variables

    private var context: Context? = null

    @Before
    fun setup() {
        context = getInstrumentation().context
    }

    @Test
    fun testWeb3instanceTest() {
        print("testWeb3 instance test")
        //MockitoAnnotations.initMocks(this)

        val veb3_ = mock(Web3::class.java)


        val web3 = Web3.getInstance(QUORUM_URL, context!!)
        val quorumHttpClientMock = mock(QuorumHttpClient::class.java)


//        Mockito.`when`(QuorumHttpClient.getCaInput(context)).thenReturn(getManualInputStram())


        //val okHttpKlient = (context, BuildConfig.QUORUM_URL)

//        val view = View(context)

//        assertNotNull(view)
        //assertNotNull(okHttpKlient)
    }

    private fun getManualInputStram(): InputStream {
        val kimlicSert = "-----BEGIN CERTIFICATE-----\n" +
                "MIIGUjCCBTqgAwIBAgIQRgpfqdk5D4G/WjwOg51XIjANBgkqhkiG9w0BAQsFADCB\n" +
                "kDELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                "A1UEBxMHU2FsZm9yZDEaMBgGA1UEChMRQ09NT0RPIENBIExpbWl0ZWQxNjA0BgNV\n" +
                "BAMTLUNPTU9ETyBSU0EgRG9tYWluIFZhbGlkYXRpb24gU2VjdXJlIFNlcnZlciBD\n" +
                "QTAeFw0xODA3MTQwMDAwMDBaFw0xOTA3MTQyMzU5NTlaMFkxITAfBgNVBAsTGERv\n" +
                "bWFpbiBDb250cm9sIFZhbGlkYXRlZDEdMBsGA1UECxMUUG9zaXRpdmVTU0wgV2ls\n" +
                "ZGNhcmQxFTATBgNVBAMMDCoua2ltbGljLmNvbTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                "ggEPADCCAQoCggEBAMghEe7xkR1qQ9OFEgotBttNPIky+Uu+wBPvUAU56w+WCMXP\n" +
                "Nk8+FkVrSlRRbE/GmXfWWB9zDTKC0TS9IfUV7PJUar8QH+ddd6IQxr0qhlQpOJ0n\n" +
                "tY0PKjZM7e0IMaBkqpyNXOeb/Zc6IiHWETuUs1NnsOCvGJioOv9NP1QJPgqmmuiM\n" +
                "ec0gMexLZhSrR8RbOAyMq3liVhuU6lydiqnMw9esW+FNVI7EG9L4tqPH95fVm8I5\n" +
                "fKNuvxAwyvi0EcHYA1scrxf2ANMb/NWBkZllz21nHFgnamn68/MtSvLT/ipXBHTJ\n" +
                "0iNCenPZhmut2EL0BSxPh5luHXDWX6YsFq+Qtm0CAwEAAaOCAtwwggLYMB8GA1Ud\n" +
                "IwQYMBaAFJCvajqUWgvYkOoSVnPfQ7Q6KNrnMB0GA1UdDgQWBBREDjZiWiyClfSW\n" +
                "UXu8A+Zssc8NoTAOBgNVHQ8BAf8EBAMCBaAwDAYDVR0TAQH/BAIwADAdBgNVHSUE\n" +
                "FjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwTwYDVR0gBEgwRjA6BgsrBgEEAbIxAQIC\n" +
                "BzArMCkGCCsGAQUFBwIBFh1odHRwczovL3NlY3VyZS5jb21vZG8uY29tL0NQUzAI\n" +
                "BgZngQwBAgEwVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2NybC5jb21vZG9jYS5j\n" +
                "b20vQ09NT0RPUlNBRG9tYWluVmFsaWRhdGlvblNlY3VyZVNlcnZlckNBLmNybDCB\n" +
                "hQYIKwYBBQUHAQEEeTB3ME8GCCsGAQUFBzAChkNodHRwOi8vY3J0LmNvbW9kb2Nh\n" +
                "LmNvbS9DT01PRE9SU0FEb21haW5WYWxpZGF0aW9uU2VjdXJlU2VydmVyQ0EuY3J0\n" +
                "MCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5jb21vZG9jYS5jb20wIwYDVR0RBBww\n" +
                "GoIMKi5raW1saWMuY29tggpraW1saWMuY29tMIIBAwYKKwYBBAHWeQIEAgSB9ASB\n" +
                "8QDvAHYA7ku9t3XOYLrhQmkfq+GeZqMPfl+wctiDAMR7iXqo/csAAAFkl+7ytwAA\n" +
                "BAMARzBFAiEAyXSc3Hk9IsuEDxO67ZhSdE2X7uKlRotGYsRAYhG/94wCIB8R/+r3\n" +
                "P4+CiSyAo8iy+x2RIOswK71SenXG9EM8OVBMAHUAdH7agzGtMxCRIZzOJU9CcMK/\n" +
                "/V5CIAjGNzV55hB7zFYAAAFkl+7zBQAABAMARjBEAiAQfNsTsBo9fS3I6DPzcP65\n" +
                "2vN1Mz72GTKat6nucWjpzQIgaEIg7rZ2ySxUu6Q6fnAL6pGvgBRkPZSLTVGO12hq\n" +
                "ZdMwDQYJKoZIhvcNAQELBQADggEBACj3ETvP1aHtcV9Ei2c35jr4NtAqd+RS9IQ5\n" +
                "m9RgXHGqbZj8WtAYY5WOYOPZVRnJNyjJ2qfqjUwsJ+m0YTWdog8f70aVLSxA8AUe\n" +
                "9CbWtsz/zuWTcMu7EXu9LJpSBv+maRNoSFaIXFfp+ePUN4CZmIce8B0vtzO8ZF6l\n" +
                "QdVwDEQh8E8NN/M6hOeAecFHMX/QyJa6nChGxtZZNaEVKQbL902SC391pfuVhzjU\n" +
                "jMb+QqHtEnnCxAtV92r+F0/nK+de85r/hFIDkQV9UlxSqGMrQw8Z5V8E9EdPLvy0\n" +
                "cjXY769i1Dm3J2nRTcUch6dwhhzvgDFwOy4TXtV3dsMhzcaPmek=\n" +
                "-----END CERTIFICATE-----"

        return ByteArrayInputStream(kimlicSert.toByteArray())
    }
}
