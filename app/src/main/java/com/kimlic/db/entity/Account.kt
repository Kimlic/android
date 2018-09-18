package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import com.kimlic.db.converter.StringListConverter


@Entity(tableName = "account",
        indices = [Index(value = arrayOf("user_id"))],
        foreignKeys = [ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)])

@TypeConverters(StringListConverter::class)
data class Account(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "user_id") var userId: Long = 0,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "status") var status: String = "",
        @ColumnInfo(name = "documents") var documents: List<String> = ArrayList(),
        @ColumnInfo(name = "logo_url") var logoUrl: String = ""
)