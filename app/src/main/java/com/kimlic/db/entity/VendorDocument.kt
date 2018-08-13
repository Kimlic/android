package com.kimlic.db.entity

import android.arch.persistence.room.*
import com.kimlic.db.converter.StringListConverter

@Entity(tableName = "vendor_document", indices = arrayOf(Index(value = arrayOf("type"), unique = true)))

@TypeConverters(StringListConverter::class)
data class VendorDocument(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Long = 0,
        @ColumnInfo(name = "contexts") var contexts: List<String> = ArrayList(),
        @ColumnInfo(name = "countries") var countries: List<String> = ArrayList(),
        @ColumnInfo(name = "description") var description: String = "",
        @ColumnInfo(name = "type") var type: String = ""
)