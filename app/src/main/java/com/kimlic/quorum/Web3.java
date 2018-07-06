package com.kimlic.quorum;

import org.web3j.protocol.Web3j;
//import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

public class Web3 {

    // Variables

    private static Web3 sInstance;

    private Web3j mWeb3;
    private Web3jService mHttpService;

    // Public

    public static Web3 getInstance(String URL) {
        if (sInstance == null)
            sInstance = new Web3(URL);

        return sInstance;
    }

    // Life

    private Web3(String URL) {
        mHttpService = new HttpService(URL);
        mWeb3 = Web3jFactory.build(mHttpService);
    }

    // Accessors

    public Web3j getWeb3() {
        return mWeb3;
    }

    public Web3jService getHttpService() {
        return mHttpService;
    }
}