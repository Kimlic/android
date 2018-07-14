package com.kimlic.quorum;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

public class QuorumKimlic {

  // Constants

  private final static String QUORUM_URL = "http://40.115.43.126:22000";
  private static String ADDRESS_ACCOUNT_STORAGE_ADAPTER = "0x5702bb159c49ad76c9998e2f4cb7707985f6ad6a";//"0xd37debc7b53d678788661c74c94f265b62a412ac";// variable

  // Variables

  private static QuorumKimlic sInstance;

  private ECKeyPair mKeyPair;
  private String mAddress;
  private Web3j mWeb3 = Web3.getInstance(QUORUM_URL).getWeb3();

  //    private SimpleStorage mSimpleStorage;

  private AccountStorageAdapter mAccountStorageAdapter;

  // Life

  private QuorumKimlic()
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    sInstance = this;

    mKeyPair = generateKeyPair();
    mAddress = generateAddress(mKeyPair);

    Credentials credentials = credentialsFrom(mKeyPair);
    mAccountStorageAdapter = loadAccountStorageAdapter(credentials, mWeb3,
        ADDRESS_ACCOUNT_STORAGE_ADAPTER);
  }

  // Public

  public TransactionReceipt setAccountFieldMainData(String UDID, String verificationType)
      throws ExecutionException, InterruptedException {
    return mAccountStorageAdapter.setAccountFieldMainData(UDID, verificationType).sendAsync().get();
//    return getAccountStorageAdapter().setAccountFieldMainData(UDID, verificationType).sendAsync()
//        .get();
  }

//  public static void setContextContract(String context_contract) {
//    ADDRESS_ACCOUNT_STORAGE_ADAPTER = context_contract;
//  }

  // Accessors

  public static QuorumKimlic getInstance() {
    if (sInstance == null) {
      try {
        sInstance = new QuorumKimlic();
      } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
        e.printStackTrace();
      }
    }

    return sInstance;
  }

  public String getAddress() {
    return mAddress;
  }

  // Private

  private ECKeyPair generateKeyPair()
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    return Keys.createEcKeyPair();
  }

  private Credentials credentialsFrom(ECKeyPair keyPair) {
    return Credentials.create(keyPair);
  }

  private String generateAddress(ECKeyPair keyPair) {
    return Numeric.prependHexPrefix(Keys.getAddress(keyPair));
  }


  private AccountStorageAdapter loadAccountStorageAdapter(Credentials credentials, Web3j web3j,
      String contractAddress) {
    return AccountStorageAdapter
        .load(contractAddress, web3j, credentials, BigInteger.ZERO, BigInteger.valueOf(4612388));
  }
}
