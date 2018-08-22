package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(tableName = "address",
        indices = [Index(value = arrayOf("user_id"), unique = true)],
        foreignKeys = [ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)])

data class Address(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "user_id") var userId: Long = 0,
        @ColumnInfo(name = "value") var value: String = "",
        @ColumnInfo(name = "state") var state: String = "",
        @ColumnInfo(name = "inserted_at") var insertedAt: Long = System.currentTimeMillis()
) {
   @Ignore constructor() : this(id = 0)
}