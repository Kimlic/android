package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*
import com.kimlic.db.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    fun insert(user: User): Long

    @Update
    fun update(user: User): Int

    @Query("SELECT * FROM user WHERE id =:id ")
    fun select(id: Long): User

    // new
    @Query("SELECT * FROM user WHERE account_address =:accountAddress")
    fun select(accountAddress: String): User

    @Query("SELECT * FROM user WHERE id =:id ")
    fun selectLive(id: Long): LiveData<User>

    // new
    @Query("SELECT * FROM user WHERE account_address =:accountAddress")
    fun selectLive(accountAddress: String): LiveData<User>

    @Query("DELETE FROM user WHERE account_address = :accountAddress")
    fun delete(accountAddress: String)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE from user")
    fun deleteAll()

}