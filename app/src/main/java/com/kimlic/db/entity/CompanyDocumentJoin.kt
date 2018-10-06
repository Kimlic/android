package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(tableName = "company_document_join",
        primaryKeys = ["company_id", "document_id"],
        foreignKeys = [
            ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["company_id"], onDelete = CASCADE, onUpdate = CASCADE),
            ForeignKey(entity = Document::class, parentColumns = ["id"], childColumns = ["document_id"], onDelete = CASCADE, onUpdate = CASCADE)
        ]
        , indices = [Index(value = ["company_id", "document_id"])]
)


data class CompanyDocumentJoin(
        @ColumnInfo(name = "company_id") var companyId: String = "",
        @ColumnInfo(name = "document_id") var documentId: Long = 0,
        @ColumnInfo(name = "verified_at") var date: Long = 0

) {
    @Ignore constructor() : this(companyId = "", documentId = 0)
}