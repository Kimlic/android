package com.kimlic.API

import com.google.gson.annotations.SerializedName

data class SyncObject(
        @SerializedName("name") var name: String?,
        @SerializedName("status") var status: String?,
        @SerializedName("verification_contract") var verificationContract: String?,
        @SerializedName("verified_on") var verifiedOn: Long
)