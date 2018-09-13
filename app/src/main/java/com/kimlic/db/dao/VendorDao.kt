package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kimlic.db.entity.VendorDocument

@Dao
interface VendorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocs(docs: List<VendorDocument>)

    @Query("SELECT * FROM vendor_document")
    fun selectLive(): LiveData<List<VendorDocument>>

    @Query("SELECT * FROM vendor_document")
    fun select(): List<VendorDocument>

    @Query("DELETE from vendor_document")
    fun deleteAll()
}