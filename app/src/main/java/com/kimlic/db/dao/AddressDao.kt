package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*
import com.kimlic.db.entity.Address

@Dao
interface AddressDao {

    @Insert(onConflict = REPLACE)
    fun insert(address: Address)

    @Update
    fun update(address: Address)

    @Query("SELECT * FROM address WHERE user_id=:userId LIMIT 1")
    fun select(userId: Long): Address

    @Query("SELECT * FROM address WHERE user_id=:userId LIMIT 1")
    fun selectLive(userId: Long): LiveData<Address>

    @Delete
    fun delete(address: Address)
}