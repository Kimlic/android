package com.kimlic.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE


@Entity(tableName = "company",
        indices = [Index(value = arrayOf("user_id")), Index(value = arrayOf("id"), unique = true)],
        foreignKeys = [ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"), onDelete = CASCADE, onUpdate = CASCADE)])

data class Company(
        @PrimaryKey
        @ColumnInfo(name = "id") var id: String = "",
        @ColumnInfo(name = "address") var address: String = "",
        @ColumnInfo(name = "details") var details: String = "",
        @ColumnInfo(name = "email") var email: String = "",
        @ColumnInfo(name = "logo") var logo: String = "",
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "phone") var phone: String = "",
        @ColumnInfo(name = "website") var website: String = "",
        @ColumnInfo(name = "application_at") var applicationAt: Long = 0,

        @ColumnInfo(name = "user_id") var userId: Long = 0,
        @ColumnInfo(name = "status") var status: String = "",
        @ColumnInfo(name = "inserted_at") var insertedAd: Long = System.currentTimeMillis()

) {
    @Ignore constructor() : this(id = "")
}