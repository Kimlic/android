package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kimlic.db.entity.Company


@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(account: Company)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(account: Company)

    @Query("SELECT * FROM company WHERE id =:id")
    fun account(id: String): Company

    @Query("SELECT C.id, C.user_id, C.address, C.details, C.email, C.logo, C. name, C.phone, C.website, C.status, C.inserted_at  FROM company as C INNER JOIN user as U ON user_id = U.id WHERE u.account_address=:accountAddress")
    fun accounts(accountAddress: String): List<Company>

    @Query("SELECT C.id, C.user_id, C.address, C.details, C.email, C.logo, C. name, C.phone, C.website, C.status, C.inserted_at FROM company as C INNER JOIN user as U ON user_id = U.id WHERE u.account_address=:accountAddress")
    fun accountsLive(accountAddress: String): LiveData<List<Company>>

    @Delete
    fun delete(account: Company)
}