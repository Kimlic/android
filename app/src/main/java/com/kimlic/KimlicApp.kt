package com.kimlic

import android.app.Application
import android.content.Context
import android.util.Log
import com.kimlic.quorum.QuorumKimlic

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

        val quorumKimlic = QuorumKimlic.getInstance()
        val address = quorumKimlic.address
        val currentValueZero = quorumKimlic.get()
        val receiptOne = quorumKimlic.setEmpty()
        val currentValueOne = quorumKimlic.get()
        val receiptTwo = quorumKimlic.set(123123)
        val currentValueTwo = quorumKimlic.get()

        Log.e(TAG, "MY QUORUM ADDRESS IS: $address")
        Log.e(TAG, "VALUE 0: $currentValueZero")
        Log.e(TAG, "RECEIPT 1: $receiptOne")
        Log.e(TAG, "VALUE 1: $currentValueOne")
        Log.e(TAG, "RECEIPT 2: $receiptTwo")
        Log.e(TAG, "VALUE 2: $currentValueTwo")
    }
}