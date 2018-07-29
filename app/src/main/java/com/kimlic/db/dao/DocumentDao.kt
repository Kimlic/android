package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.kimlic.db.entity.Document

@Dao
interface DocumentDao {

    @Insert(onConflict = REPLACE)
    fun insert(document: Document): Long

    @Update
    fun update(document: Document)

    @Query("SELECT * FROM document WHERE user_id =:userId AND type=:type LIMIT 1")
    fun selectByUserIdAndType(userId: Long, type: String): Document

    @Query("SELECT * FROM document WHERE user_id =:userId")
    fun selectByUserIdLive(userId: Long): LiveData<List<Document>>

    // New

    @Query("SELECT * FROM document INNER JOIN user on document.user_id = user_id WHERE user.account_address =:accountAddress")
    fun selectLive(accountAddress: String): LiveData<List<Document>>

    @Query("DELETE FROM document WHERE id = :id")
    fun delete(id: Long)

    @Delete
    fun deleteDocument(document: Document)
}