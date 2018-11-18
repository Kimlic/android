package com.kimlic.quorum

import android.content.Context
import android.util.Log
import com.kimlic.BuildConfig
import com.kimlic.quorum.bip44.HdKeyNode
import com.kimlic.quorum.bip44.hdpath.HdKeyPath
import com.kimlic.quorum.contracts.AccountStorageAdapter
import com.kimlic.quorum.contracts.KimlicContractsContext
import com.kimlic.quorum.contracts.KimlicTokenContract
import com.kimlic.quorum.crypto.MnemonicUtils
import com.kimlic.quorum.crypto.SecureRandomTools
import com.kimlic.utils.allopen.TestOpen
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.concurrent.ExecutionException

@TestOpen
class QuorumKimlic private constructor(val mnemonic: String, context: Context) {

    // Variables

    private val mWeb3: Web3j
    val walletAddress: String
    private val mCredentials: Credentials
    private var mAccountStorageAdapter: AccountStorageAdapter? = null
    private var mKimlicContractsContext: KimlicContractsContext? = null
    private var mKimlicTokenContract: KimlicTokenContract? = null

    // Init

    init {
        sInstance = this
        mWeb3 = Web3.getInstance(QUORUM_URL, context).web3
        val keyPair = generateKeyPair(this.mnemonic)
        walletAddress = generateAddress(keyPair)
        mCredentials = credentialsFrom(keyPair)
    }

    // Companion

    companion object {
        private const val QUORUM_URL = BuildConfig.QUORUM_URL
        private var sInstance: QuorumKimlic? = null

        @Throws(Exception::class)
        fun createInstance(mnemonic_: String?, context: Context?): QuorumKimlic {
            var mnemonic = mnemonic_
            when {
                sInstance != null -> throw Exception("createInstance should be called once")
                context == null -> throw IllegalArgumentException("Context is null")
                mnemonic == null -> mnemonic = generateMnemonic(context)
            }

            return QuorumKimlic(mnemonic!!, context!!)
        }

        fun destroyInstance() {
            if (sInstance != null) sInstance = null
        }

        // Accessors

        fun getInstance(mnemonic: String, context: Context): QuorumKimlic {
            if (sInstance == null) {
                Log.d("TAGQUORUM", "quorun create new instance")
                sInstance = QuorumKimlic(mnemonic, context)
                //      throw new Exception("Call createInstance to generate instance");
            }

            return sInstance!!
        }

        private fun generateMnemonic(context: Context): String {
            MnemonicUtils.initWordList(context)

            val initialEntropy = ByteArray(16)
            val secureRandom = SecureRandomTools.secureRandom()
            secureRandom.nextBytes(initialEntropy)

            return MnemonicUtils.generateMnemonic(initialEntropy)
        }

        private fun createBip44NodeFromSeed(seed: ByteArray, path: String): ECKeyPair {
            val p = HdKeyPath.valueOf(path)
            var node = HdKeyNode.fromSeed(seed)
            node = node.createChildNode(p)
            val privateKeyByte = node.privateKey.privateKeyBytes

            return ECKeyPair.create(privateKeyByte)
        }
    }

    // Quorum Calls

    val accountStorageAdapter: String
        @Throws(ExecutionException::class, InterruptedException::class)
        get() {
            if (mKimlicContractsContext == null) throw InterruptedException("Empty contract address")

            return mKimlicContractsContext!!.accountStorageAdapter.sendAsync().get()
        }

    /*
   * Return address KimlicToken contract
   * */
    val kimlicTokenAddress: String
        @Throws(ExecutionException::class, InterruptedException::class)
        get() = mKimlicContractsContext!!.kimlicToken.sendAsync().get()

    // Quorum Transactions

    @Throws(ExecutionException::class, InterruptedException::class)
    fun setFieldMainData(value: String, type: String): TransactionReceipt {
        if (mAccountStorageAdapter == null) throw InterruptedException("Empty contract address")

        val data = "{\"$type\": \"$value\"}"
        return mAccountStorageAdapter!!.setFieldMainData(data, type).sendAsync().get()
        //    return mAccountStorageAdapter.setAccountFieldMainData(UDID, verificationType).sendAsync().get();
    }

    fun setKimlicContractsContextAddress(address: String) {
        mKimlicContractsContext = KimlicContractsContext.load(address, mWeb3, mCredentials, BigInteger.ZERO, BigInteger.valueOf(4612388))
    }

    fun setAccountStorageAdapterAddress(address: String) {
        mAccountStorageAdapter = AccountStorageAdapter.load(address, mWeb3, mCredentials, BigInteger.ZERO, BigInteger.valueOf(4612388))
    }

    /*
   * Create instance KimlicToken contract; input parameter - address from KimlicContractsContext
   * */

    fun setKimlicToken(address: String) {
        mKimlicTokenContract = KimlicTokenContract.load(address, mWeb3, mCredentials, BigInteger.ZERO, BigInteger.valueOf(4612388))
    }

    /*
   * Kimlic token contract returns quantity of tokens
   * */

    @Throws(Exception::class)
    fun getTokenBalance(owner: String): BigInteger {
        return mKimlicTokenContract!!.balanceOf(owner).sendAsync().get()
    }


    private fun generateKeyPair(mnemonic: String): ECKeyPair {
        val path = "m/44'/60'/0'/0/0"
        val passphrase = "kimlic"
        val seed = MnemonicUtils.generateSeed(mnemonic, passphrase)

        return createBip44NodeFromSeed(seed, path)
    }

    private fun credentialsFrom(keyPair: ECKeyPair): Credentials {
        return Credentials.create(keyPair)
    }

    private fun generateAddress(keyPair: ECKeyPair): String {
        return Numeric.prependHexPrefix(Keys.getAddress(keyPair))
    }
}