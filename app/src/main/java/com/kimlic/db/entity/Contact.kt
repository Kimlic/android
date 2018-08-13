package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

//        indices = arrayOf(Index(name = "user_id", unique = true)),

@Entity(tableName = "contact"
        , indices = arrayOf(Index(value = arrayOf("type"), unique = true))
        , foreignKeys = arrayOf(ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)))

data class Contact(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "user_id") var userId: Long = 0,
        @ColumnInfo(name = "type") var type: String = "",
        @ColumnInfo(name = "value") var value: String = "",
        @ColumnInfo(name = "approved") var approved: Boolean = false,
        @ColumnInfo(name = "inserted_at") var inserteAt: Long = System.currentTimeMillis()
)