package com.kimlic.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(
        @PrimaryKey @ColumnInfo(name = "id") var id: Long?,
        @ColumnInfo(name = "phone") var phone: String = "",
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "last_name") var lastName: String = "",
        @ColumnInfo(name = "email") var email: String = "",
        @ColumnInfo(name = "address") var address: String = "",
        @ColumnInfo(name = "blockchain_address") var blockchainAddress: String = "",
        @ColumnInfo(name = "mnemonic") var mnemonic: String = "",

        @ColumnInfo(name = "portrait_file_path") var portraitFilePath: String = "",
        @ColumnInfo(name = "portrait_mini_icon_file_path") var portraitMiniFilePath: String = ""

)