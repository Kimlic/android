package com.kimlic.quorum

object EthereumAddressValidator {

    fun isValidAddress(address: String): Boolean {
        val regex = "^0x[0-9a-f]{40}$"
        return address.matches(regex.toRegex())
    }
}