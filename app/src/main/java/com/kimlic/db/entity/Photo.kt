package com.kimlic.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.*
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "photo"
        ,foreignKeys = [ForeignKey(entity = Document::class, parentColumns = arrayOf("id"), childColumns = arrayOf("document_id"), onDelete = CASCADE, onUpdate = CASCADE), ForeignKey(entity = Address::class, parentColumns = arrayOf("id"), childColumns = arrayOf("address_id"), onDelete = CASCADE, onUpdate = CASCADE)]
)
data class Photo(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long? = null,
        @ColumnInfo(name = "document_id") var documentId: Long? = null,
        @ColumnInfo(name = "address_id") var addressId: Long? = null,
        @ColumnInfo(name = "file") var file: String = "",
        @ColumnInfo(name = "type") var type: String = "front",
        @ColumnInfo(name = "inserted_at") var insertedAt: Long = System.currentTimeMillis()
)