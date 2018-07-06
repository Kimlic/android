package com.kimlic.quorum;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

public class QuorumKimlic {

    // Constants

    private final static String QUORUM_URL = "http://05357055.ngrok.io";
    private final static String ADDRESS_SIMPLE_STORAGE = "0x238e461d596a8416b73e8dd3c75789aeaa81edeb";

    // Variables

    private static QuorumKimlic sInstance;

    private ECKeyPair mKeyPair;
    private String mAddress;
    private Web3j mWeb3 = Web3.getInstance(QUORUM_URL).getWeb3();

    private SimpleStorage mSimpleStorage;

    // Life

    private QuorumKimlic() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        sInstance = this;

        mKeyPair = generateKeyPair();
        mAddress = generateAddress(mKeyPair);

        Credentials credentials = credentialsFrom(mKeyPair);
        mSimpleStorage = loadSimpleStorage(credentials, mWeb3, ADDRESS_SIMPLE_STORAGE);
    }

    // Public

    public TransactionReceipt setEmpty() throws ExecutionException, InterruptedException {
        return mSimpleStorage.setEmpty().sendAsync().get();
    }

    public int get() throws ExecutionException, InterruptedException {
        return mSimpleStorage.get().sendAsync().get().intValue();
    }

    public TransactionReceipt set(int value) throws ExecutionException, InterruptedException {
        return mSimpleStorage.set(BigInteger.valueOf(value)).sendAsync().get();
    }

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

    private ECKeyPair generateKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Keys.createEcKeyPair();
    }

    private Credentials credentialsFrom(ECKeyPair keyPair) {
        return Credentials.create(keyPair);
    }

    private String generateAddress(ECKeyPair keyPair) {
        return Numeric.prependHexPrefix(Keys.getAddress(keyPair));
    }

    private SimpleStorage loadSimpleStorage(Credentials credentials, Web3j web3j, String contractAddress) {
        return SimpleStorage.load(contractAddress, web3j, credentials, BigInteger.ZERO, BigInteger.valueOf(4612388));
    }
}
