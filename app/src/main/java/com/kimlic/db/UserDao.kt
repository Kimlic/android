package com.kimlic.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Query("SELECT * FROM user WHERE id = :id")
    fun findById(id: Long): User

    @Query("SELECT * FROM user WHERE id = :id")
    fun findByIdLive(id: Long): LiveData<User>


    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE from user")
    fun deleteAll()
}