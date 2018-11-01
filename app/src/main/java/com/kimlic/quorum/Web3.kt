package com.kimlic.quorum

import android.content.Context
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory

class Web3

// Private

private constructor(context: Context, URL: String) {

    // Accessors

    val web3: Web3j = Web3jFactory.build(QuorumHttpClient(context, URL))

    companion object {

        // Variables

        private var sInstance: Web3? = null

        // Public

        fun getInstance(URL: String, context: Context): Web3 {
            if (sInstance == null)
                sInstance = Web3(context, URL)

            return sInstance!!
        }
    }
}