package com.kimlic.service.entity

import com.google.gson.annotations.SerializedName

data class CompanyDocumentsEntity(
        @SerializedName("data") var docs: List<DocumentWrapperEntity>
)