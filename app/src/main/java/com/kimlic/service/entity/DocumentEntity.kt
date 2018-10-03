package com.kimlic.service.entity

import com.google.gson.annotations.SerializedName

data class DocumentEntity(
        @SerializedName("first_name") var firstName: String = "",
        @SerializedName("last_name") var lastName: String = "",
        @SerializedName("verified_at") var verifiedAt: String = "",
        @SerializedName("type") var type: String = "",
        @SerializedName("country") var countryIso: String = ""
)