package com.kimlic.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "contact",
//        indices = arrayOf(Index(name = "user_id", unique = true)),
        foreignKeys = arrayOf(ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
)

data class Contact(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "user_id") var userId: Long,
        @ColumnInfo(name = "type") var type: String,
        @ColumnInfo(name = "value") var value: String = "",
        @ColumnInfo(name = "approved") var approved: Boolean = false,
        @ColumnInfo(name = "inserted_at") var inserteAt: Long = System.currentTimeMillis()
)
