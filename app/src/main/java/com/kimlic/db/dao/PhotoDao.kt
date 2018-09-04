package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
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

    @Query("SELECT P.id, P.document_id, P.address_id, P.user_id, P.type, P.file, P.inserted_at, P.synced FROM photo as P INNER JOIN document ON (p.document_id = document.id AND document.type=:documentType )INNER JOIN user ON document.user_id = user.id WHERE user.account_address =:accountAddress")
    fun selectUserPhotosByDocument(accountAddress: String, documentType: String): List<Photo>

    @Query("SELECT P.id, P.document_id, P.address_id, P.user_id, P.type, P.file, P.inserted_at, P.synced FROM photo as P INNER JOIN address ON p.address_id = address.id INNER JOIN user as U ON address.user_id = U.id WHERE U.account_address =:accountAddress")
    fun selectUserAddressPhoto(accountAddress: String): List<Photo>

    @Query("SELECT P.file FROM photo as P INNER JOIN document as D ON P.document_id = D.id INNER JOIN user as U ON D.user_id = U.id WHERE account_address=:accountAddress UNION SELECT p.file FROM photo as P INNER JOIN address as A ON P.address_id = A.id WHERE address_id IN (SELECT address_id FROM user WHERE account_address=:accountAddress) UNION SELECT P.file FROM photo as P INNER JOIN user as U WHERE P.type IN('portrait', 'portrait_preview') AND account_address=:accountAddress")
    fun profilePhotos(accountAddress: String): List<String>

    @Query("SELECT P.id, P.document_id, P.address_id, P.user_id, P.type, P.file, P.inserted_at, P.synced FROM photo as P INNER JOIN user as U ON p.user_id = U.id WHERE P.type IN('portrait', 'portrait_preview') AND account_address=:accountAddress")
    fun portraitPhotosLive(accountAddress: String): LiveData<List<Photo>>

    @Delete
    fun delete(photo: Photo)
}