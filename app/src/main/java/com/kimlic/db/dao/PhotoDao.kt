package com.kimlic.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.kimlic.db.entity.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = REPLACE)
    fun insert(photos: List<Photo>): List<Long>

    @Insert(onConflict = REPLACE)
    fun insert(photo: Photo)

    @Update
    fun update(photo: Photo): Int

    @Query("SELECT P.id, P.document_id, P.type, P.file, P.inserted_at FROM photo as P INNER JOIN document ON (p.document_id = document.id AND document.type=:documentType )INNER JOIN user ON document.user_id = user.id WHERE user.account_address =:accountAddress")//INNER JOIN document ON photo.document_id = document.id ")
    fun selectUserPhotosByDocument(accountAddress: String, documentType: String): List<Photo>

    @Delete
    fun delete(photo: Photo)
}