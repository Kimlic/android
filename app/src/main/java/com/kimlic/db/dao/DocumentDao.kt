package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.kimlic.db.entity.Document

@Dao
interface DocumentDao {

    @Insert(onConflict = REPLACE)
    fun insert(document: Document): Long

    @Update(onConflict = REPLACE)
    fun update(document: Document)

    @Query("SELECT * FROM document INNER JOIN user on document.user_id = user_id WHERE user.account_address =:accountAddress")
    fun selectLive(accountAddress: String): LiveData<List<Document>>

    @Query("SELECT D.id, D.user_id, D.type, D.value, D.state, D.country, D.document_number, D.expire_date, D.iserted_at FROM document as D INNER JOIN user on D.user_id = user_id WHERE user.account_address =:accountAddress")
    fun select(accountAddress: String): List<Document>

    @Query("SELECT D.id, D.user_id, D.type, D.value, D.state, D.country, D.document_number, D.expire_date, D.iserted_at FROM document as D INNER JOIN user on D.user_id = user_id WHERE user.account_address =:accountAddress AND type = :documentType LIMIT 1")
    fun select_(accountAddress: String, documentType: String): Document

    @Query("DELETE FROM document WHERE id = :id")
    fun delete(id: Long)

    @Delete
    fun deleteDocument(document: Document)
}