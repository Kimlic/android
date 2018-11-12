package com.kimlic.quorum

import android.content.Context
import com.kimlic.utils.allopen.TestOpen
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory

@TestOpen
class Web3 private constructor(context: Context, URL: String) {

    val web3: Web3j = Web3jFactory.build(QuorumHttpClient(context, URL))

    // Companion

    companion object {
        private var sInstance: Web3? = null

        fun getInstance(URL: String, context: Context): Web3 {
            if (sInstance == null)
                sInstance = Web3(context, URL)

            return sInstance!!
        }
    }
}