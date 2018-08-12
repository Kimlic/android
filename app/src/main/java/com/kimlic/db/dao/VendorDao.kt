package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kimlic.db.entity.VendorDocument
import com.kimlic.vendors.Document_

@Dao
interface VendorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocs(docs: List<VendorDocument>)

    @Query("SELECT * FROM vendor_document")
    fun select(): LiveData<List<VendorDocument>>
}