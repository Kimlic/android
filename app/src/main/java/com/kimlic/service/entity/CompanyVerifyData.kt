package com.kimlic.service.entity

import com.google.gson.annotations.SerializedName

data class CompanyVerifyData(
        @SerializedName("document") var data: CompanyVerifyData
)