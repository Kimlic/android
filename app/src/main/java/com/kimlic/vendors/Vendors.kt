package com.kimlic.vendors

import com.google.gson.annotations.SerializedName

data class Vendors(
        @SerializedName(value = "documents") var documents: List<Document_>
)
