package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(tableName = "document",
        indices = [Index(value = arrayOf("type"), unique = true), Index(value = arrayOf("user_id"))],
        foreignKeys = [ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)])

data class Document(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "user_id") var userId: Long = 0,
        @ColumnInfo(name = "document_number") var number: String = "",
        @ColumnInfo(name = "expire_date") var expireDate: String = "",
        @ColumnInfo(name = "country") var country: String = "",
        @ColumnInfo(name = "value") var value: String = "",
        @ColumnInfo(name = "state") var state: String = "",
        @ColumnInfo(name = "type") var type: String = "",
        @ColumnInfo(name = "inserted_at") var insertedAt: Long = System.currentTimeMillis()
) {
    @Ignore constructor() : this(id = 0)
}
