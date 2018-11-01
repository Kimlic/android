package com.kimlic.quorum.java;

import android.content.Context;
import com.kimlic.quorum.QuorumHttpClient;
import org.web3j.protocol.Web3jFactory;

//import org.web3j.protocol.Web3jFactory;

public class Web3_j {

  // Variables

  private static Web3_j sInstance;

  private org.web3j.protocol.Web3j mWeb3;

  // Public

  public static Web3_j getInstance(String URL, Context context) {
    if (sInstance == null)
      sInstance = new Web3_j(context, URL);

    return sInstance;
  }

  // Accessors

  public org.web3j.protocol.Web3j getWeb3() {
    return mWeb3;
  }

  // Private

  private Web3_j(Context context, String URL) {
    mWeb3 = Web3jFactory.build(new QuorumHttpClient(context, URL));
  }
}