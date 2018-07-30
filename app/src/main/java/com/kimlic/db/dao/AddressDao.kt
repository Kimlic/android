package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*
import com.kimlic.db.entity.Address

@Dao
interface AddressDao {

    @Insert(onConflict = REPLACE)
    fun insert(address: Address): Long

    @Update
    fun update(address: Address)

//    @Query("SELECT * FROM address WHERE user_id=:userId LIMIT 1")
//    fun select(userId: Long): Address
//
//    @Query("SELECT * FROM address WHERE user_id=:userId LIMIT 1")
//    fun selectLive(userId: Long): LiveData<Address>

    // new

    @Query("SELECT A.id, A.user_id, A.state, A.value FROM address as A INNER JOIN user ON A.user_id = user.id WHERE user.account_address =:accountAddress ")
//    @Query("SELECT * FROM address INNER JOIN user ON address.user_id = user.id WHERE user.account_address =:accountAddress ")
    fun selectLive(accountAddress: String): LiveData<Address>

    @Query("SELECT A.id, A.user_id, A.state, A.value FROM address as A INNER JOIN user ON A.user_id = user.id WHERE user.account_address =:accountAddress ")
    fun select(accountAddress: String): Address

    @Query("DELETE FROM address WHERE address.id =:addressId")
    fun delete(addressId: Long)

    @Delete
    fun delete(address: Address)

}

//@Query("SELECT P.id, P.document_id, P.type, P.file, P.inserted_at FROM photo as P INNER JOIN document ON (p.document_id = document.id AND document.type=:documentType )INNER JOIN user ON document.user_id = user.id WHERE user.account_address =:accountAddress")//INNER JOIN document ON photo.document_id = document.id ")
