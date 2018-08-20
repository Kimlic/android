package com.kimlic.quorum;

import android.content.Context;
import com.kimlic.BuildConfig;
import com.kimlic.quorum.bip44.HdKeyNode;
import com.kimlic.quorum.bip44.hdpath.HdKeyPath;
import com.kimlic.quorum.contracts.AccountStorageAdapter;
import com.kimlic.quorum.contracts.KimlicContractsContext;
import com.kimlic.quorum.crypto.MnemonicUtils;
import com.kimlic.quorum.crypto.SecureRandomTools;
import com.kimlic.API.KimlicApi;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

public class QuorumKimlic {

  // Constants

  //  private final static String QUORUM_URL = "http://40.115.43.126:22000";
  private final static String BASE_URL = BuildConfig.BASE_URL;
  private final static String QUORUM_URL = BASE_URL + KimlicApi.QUORUM.getPath();

  // Variables

  private static QuorumKimlic sInstance;

  private Web3j mWeb3;
  private String mWalletAddress;
  private Credentials mCredentials;
  private String mMnemonic;
  private AccountStorageAdapter mAccountStorageAdapter;
  private KimlicContractsContext mKimlicContractsContext;

  // Public

  public static QuorumKimlic createInstance(String mnemonic, Context context) throws Exception {
    if (sInstance != null) {
      throw new Exception("createInstance should be called once");
    }

    if (context == null) {
      throw new IllegalArgumentException("Context is null");
    }

    if (mnemonic == null) {
      mnemonic = generateMnemonic(context);
    }

    return new QuorumKimlic(mnemonic, context);
  }

  public static void destroyInstance() {
    if (sInstance != null) {
      sInstance = null;
    }
  }

  // Quorum Calls

  public String getAccountStorageAdapter() throws ExecutionException, InterruptedException {
    if (mKimlicContractsContext == null) {
      throw new InterruptedException("Empty contract address");
    }

    return mKimlicContractsContext.getAccountStorageAdapter().sendAsync().get();
  }

  // Quorum Transactions

  public TransactionReceipt setFieldMainData(String value, String type)
      throws ExecutionException, InterruptedException {
    if (mAccountStorageAdapter == null) {
      throw new InterruptedException("Empty contract address");
    }

    String data = "{\"" + type + "\": \"" + value + "\"}";
    return mAccountStorageAdapter.setFieldMainData(data, type).sendAsync().get();
//    return mAccountStorageAdapter.setAccountFieldMainData(UDID, verificationType).sendAsync().get();
  }

  // Accessors

  public static QuorumKimlic getInstance() throws Exception {
    if (sInstance == null) {
      throw new Exception("Call createInstance to generate instace");
    }

    return sInstance;
  }

  public String getWalletAddress() {
    return mWalletAddress;
  }

  public String getMnemonic() {
    return mMnemonic;
  }

  public void setKimlicContractsContextAddress(String address) {
    mKimlicContractsContext = KimlicContractsContext.load(address, mWeb3, mCredentials, BigInteger.ZERO, BigInteger.valueOf(4612388));
  }

  public void setAccountStorageAdapterAddress(String address) {
    mAccountStorageAdapter = AccountStorageAdapter
        .load(address, mWeb3, mCredentials, BigInteger.ZERO, BigInteger.valueOf(4612388));
  }

  // Private

  private QuorumKimlic(String mnemonic, Context context) {
    sInstance = this;
    mMnemonic = mnemonic;

//    mWeb3 = Web3.getInstance(QUORUM_URL).getWeb3();
    mWeb3 = Web3.getInstance(QUORUM_URL, context).getWeb3();
    ECKeyPair keyPair = generateKeyPair(mMnemonic);
    mWalletAddress = generateAddress(keyPair);
    mCredentials = credentialsFrom(keyPair);
  }

  private static String generateMnemonic(Context context) {
    MnemonicUtils.initWordList(context);

    byte[] initialEntropy = new byte[16];
    SecureRandom secureRandom = SecureRandomTools.secureRandom();
    secureRandom.nextBytes(initialEntropy);

    return MnemonicUtils.generateMnemonic(initialEntropy);
  }

  private ECKeyPair generateKeyPair(String mnemonic) {
    String path = "m/44'/60'/0'/0/0";
    String passphrase = "kimlic";
    byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase);

    return createBip44NodeFromSeed(seed, path);
  }

  private static ECKeyPair createBip44NodeFromSeed(byte[] seed, String path) {
    HdKeyPath p = HdKeyPath.valueOf(path);
    HdKeyNode node = HdKeyNode.fromSeed(seed);
    node = node.createChildNode(p);
    byte[] privateKeyByte = node.getPrivateKey().getPrivateKeyBytes();

    return ECKeyPair.create(privateKeyByte);
  }

  private Credentials credentialsFrom(ECKeyPair keyPair) {
    return Credentials.create(keyPair);
  }

  private String generateAddress(ECKeyPair keyPair) {
    return Numeric.prependHexPrefix(Keys.getAddress(keyPair));
  }
}