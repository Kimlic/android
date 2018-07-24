package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*
import com.kimlic.db.entity.Contact

@Dao
interface ContactDao {

    @Insert(onConflict = REPLACE)
    fun insert(contact: Contact): Long

    @Update
    fun update(contact: Contact): Int

    @Query("SELECT * FROM contact WHERE user_id = :userId")
    fun selectLive(userId: Long): LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE user_id = :userId AND type =:type LIMIT 1")
    fun selectByUserIdAndType(userId: Long, type: String): Contact

    @Delete
    fun delete(contact: Contact)
}