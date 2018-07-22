package com.kimlic.db.entity

import android.arch.persistence.room.*

@Entity(tableName = "user")

data class User(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "first_name") var firstName: String = "",
        @ColumnInfo(name = "last_name") var lastName: String = "",
        @ColumnInfo(name = "mnemonic") var mnemonic: String = "",
        @ColumnInfo(name = "portrait_file") var portraitFile: String = "",
        @ColumnInfo(name = "wallet_address") var walletAddress: String = "",
        @ColumnInfo(name = "inserted_at") var insertedAt: Long = System.currentTimeMillis()
)