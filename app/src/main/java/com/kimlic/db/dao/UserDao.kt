package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kimlic.db.entity.Address
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User

@Dao
interface UserDao {

    // User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Update
    fun updateUser(user: User): Int

    @Query("SELECT * FROM user WHERE id =:id ")
    fun selectUserById(id: Long): User

    @Query("SELECT * FROM user WHERE id =:id ")
    fun selectUserByIdLive(id: Long): LiveData<User>

    // Contact

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: Contact): Long

    @Update
    fun update(contact: Contact): Int

    @Query("SELECT * FROM contact WHERE user_id = :userId")
    fun selectUserContactsLive(userId: Long): LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE user_id = :userId AND type =:type LIMIT 1")
    fun selectContactByUserIdAndType(userId: Long, type: String): Contact

    // Document

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(document: Document)

    @Update
    fun update(document: Document)

    @Query("SELECT * FROM document WHERE user_id =:userId AND type=:type LIMIT 1")
    fun selectDocumentByUserIdAndType(userId: Long, type: String): Document

    @Query("SELECT * FROM document WHERE user_id =:userId")
    fun selectDocument(userId: Long): LiveData<List<Document>>

    // Address

    @Insert()
    fun insert(address: Address)

    @Update
    fun update(address: Address)

    @Query("SELECT * FROM address WHERE user_id=:userId LIMIT 1")
    fun selectAddres(userId: Long): Address

    @Query("SELECT * FROM address WHERE user_id=:userId LIMIT 1")
    fun selectAddressLive(userId: Long): LiveData<Address>

    // Delete

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE from user")
    fun deleteAll()

}