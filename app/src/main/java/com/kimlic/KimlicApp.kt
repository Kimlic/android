package com.kimlic

import android.app.Application
import android.content.Context
import android.util.Log
import com.kimlic.quorum.QuorumKimlic
import java.util.*
import android.provider.SyncStateContract.Helpers.update
import com.kimlic.quorum.DeviceID
import com.kimlic.quorum.Sha
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class KimlicApp : Application() {

    // Constants

    private val TAG = this.javaClass.simpleName

    // Companion

    companion object {
        private var instance: KimlicApp? = null

        fun applicationContext(): Context = instance!!.applicationContext
    }

    // Life

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        val quorumKimlic = QuorumKimlic.getInstance()
//        val address = quorumKimlic.address
//        val currentValueZero = quorumKimlic.get()
//        val receiptOne = quorumKimlic.setEmpty()
//        val currentValueOne = quorumKimlic.get()
//        val receiptTwo = quorumKimlic.set(123123)
//        val currentValueTwo = quorumKimlic.get()
//
//        Log.e(TAG, "MY QUORUM ADDRESS IS: $address")
//        Log.e(TAG, "VALUE 0: $currentValueZero")
//        Log.e(TAG, "RECEIPT 1: $receiptOne")
//        Log.e(TAG, "VALUE 1: $currentValueOne")
//        Log.e(TAG, "RECEIPT 2: $receiptTwo")
//        Log.e(TAG, "VALUE 2: $currentValueTwo")

        val UDID = DeviceID.id(applicationContext)
        val shaUDID = Sha.sha256(UDID)// saves to preferences
        val address = quorumKimlic.address

        Log.e(TAG, "UDID: $UDID")
        Log.e(TAG, "SHA UDID: $shaUDID")
        Log.e(TAG, "QUORUM ADDRESS: $address")

        val receiptEmail = quorumKimlic.setAccountFieldMainData(Sha.sha256("dmytro@pharosproduction.com"), "email")
        Log.e(TAG, "RECEIPT: $receiptEmail")

        val receiptPhone = quorumKimlic.setAccountFieldMainData(Sha.sha256("+380997762791"), "phone")
        Log.e(TAG, "RECEIPT: $receiptPhone")
    }
}