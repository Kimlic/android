package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(tableName = "photo"
        , indices = [Index(value = arrayOf("id"), unique = true), Index(value = arrayOf("document_id")), Index(value = arrayOf("address_id")), Index(value = arrayOf("user_id"))]
        , foreignKeys = [
    ForeignKey(entity = Document::class, parentColumns = arrayOf("id"), childColumns = arrayOf("document_id"), onDelete = CASCADE, onUpdate = CASCADE),
    ForeignKey(entity = Address::class, parentColumns = arrayOf("id"), childColumns = arrayOf("address_id"), onDelete = CASCADE, onUpdate = CASCADE),
    ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)]
)
data class Photo(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long? = null,
        @ColumnInfo(name = "document_id") var documentId: Long? = null,
        @ColumnInfo(name = "address_id") var addressId: Long? = null,
        @ColumnInfo(name = "user_id") var userId: Long? = null,
        @ColumnInfo(name = "file") var file: String = "",
        @ColumnInfo(name = "type") var type: String = "front",
        @ColumnInfo(name = "inserted_at") var insertedAt: Long = System.currentTimeMillis(),
        @ColumnInfo(name = "synced") var synced: Boolean = false
) {
    @Ignore constructor() : this(id = 0)
}