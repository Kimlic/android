package com.kimlic.vendors

import com.google.gson.annotations.SerializedName

data class Document_(
        @SerializedName(value = "contexts") var contexts: List<String>? = null,
        @SerializedName(value = "countries") var countries: List<String>? = null,
        @SerializedName(value = "description") var description: String? = null,
        @SerializedName(value = "type") var type: String? = null
)