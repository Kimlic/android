package com.kimlic.vendors.entity

import com.google.gson.annotations.SerializedName

data class Company_(
        @SerializedName("address") var address: String = "",
        @SerializedName("details") var details: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("logo") var logo: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("phone") var phone: String = "",
        @SerializedName("website") var website: String = ""
)