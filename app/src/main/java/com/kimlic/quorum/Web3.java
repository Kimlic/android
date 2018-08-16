package com.kimlic.quorum;

import android.content.Context;

import org.web3j.protocol.Web3j;
//import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

public class Web3 {

  // Variables

  private static Web3 sInstance;

  private Web3j mWeb3;

  // Public

  public static Web3 getInstance(String URL, Context context) {
    if (sInstance == null)
      sInstance = new Web3(context, URL);

    return sInstance;
  }

  // Accessors

  public Web3j getWeb3() {
    return mWeb3;
  }

  // Private

  private Web3(Context context, String URL) {
    mWeb3 = Web3jFactory.build(new QuorumHttpClient(context, URL));
  }
}