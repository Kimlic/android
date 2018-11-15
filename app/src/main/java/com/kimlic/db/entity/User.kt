package com.kimlic.db.entity

import android.arch.persistence.room.*

@Entity(tableName = "user")


data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "first_name") var firstName: String = "",
        @ColumnInfo(name = "last_name") var lastName: String = "",
        @ColumnInfo(name = "kim_quantity") var kimQuantity: Int = 0,
        @ColumnInfo(name = "mnemonic") var mnemonic: String = "",
        @ColumnInfo(name = "portrait_file") var portraitFile: String = "",
        @ColumnInfo(name = "portrait_preview_file") var portraitPreviewFile: String = "",
        @ColumnInfo(name = "account_address") var accountAddress: String = "",
        @ColumnInfo(name = "inserted_at") var insertedAt: Long = System.currentTimeMillis()
) {
    @Ignore constructor() : this(id = 0)
}