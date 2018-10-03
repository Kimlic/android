package com.kimlic.service.entity

import com.google.gson.annotations.SerializedName

data class DocumentWrapperEntity(
        @SerializedName("document") var doument: DocumentEntity
)