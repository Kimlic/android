package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*
import com.kimlic.db.entity.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = REPLACE)
    fun insert(photos: List<Photo>): List<Long>

    @Update
    fun update(photo: Photo): Int

    @Query("SELECT * FROM photo WHERE document_id = :documentId")
    fun selectPhotosLive(documentId: Long): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE document_id = :documentId AND side =:side LIMIT 1")
    fun selectByDocumentIdAndType(documentId: Long, side: String): Photo

    @Delete
    fun delete(photo: Photo)
}