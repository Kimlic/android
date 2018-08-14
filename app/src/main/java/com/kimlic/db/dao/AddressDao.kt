package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.kimlic.db.entity.Address

@Dao
interface AddressDao {

    @Insert(onConflict = REPLACE)
    fun insert(address: Address): Long

    @Update
    fun update(address: Address)

    @Query("SELECT A.id, A.user_id, A.state, A.value, A.inserted_at FROM address as A INNER JOIN user ON A.user_id = user.id WHERE user.account_address =:accountAddress ")
    fun selectLive(accountAddress: String): LiveData<Address>

    @Query("SELECT A.id, A.user_id, A.state, A.value, A.inserted_at FROM address as A INNER JOIN user ON A.user_id = user.id WHERE user.account_address =:accountAddress ")
    fun select(accountAddress: String): Address

    @Query("DELETE FROM address WHERE address.id =:addressId")
    fun delete(addressId: Long)

    @Delete
    fun delete(address: Address)
}