package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kimlic.db.entity.Account


@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(account: Account)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(account: Account)

    @Query("SELECT * FROM account WHERE id =:id")
    fun account(id: Long): Account

    @Query("SELECT A.user_id, A.id, A.documents, A.logo_url, A.name, A.status FROM account as A INNER JOIN user as U ON user_id = U.id WHERE u.account_address=:accountAddress")
    fun accounts(accountAddress: String): List<Account>

    @Query("SELECT A.id, A.user_id, A.id, A.documents, A.logo_url, A.name, A.status FROM account as A INNER JOIN user as U ON user_id = U.id WHERE u.account_address=:accountAddress")
    fun accountsLive(accountAddress: String): LiveData<List<Account>>

    @Delete
    fun delete(account: Account)
}