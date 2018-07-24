package com.kimlic.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "document",
        foreignKeys = arrayOf(ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)))

data class Document(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "user_id") var userId: Long = 0,
        @ColumnInfo(name = "value") var value: String = "",
        @ColumnInfo(name = "state") var state: String = "",
        @ColumnInfo(name = "type") var type: String = "",
        @ColumnInfo(name = "iserted_at") var insertedAt: Long = System.currentTimeMillis()
)

// 4 states: "", "pending", "approved", "regected"