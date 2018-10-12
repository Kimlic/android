package com.kimlic.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kimlic.db.entity.CompanyDocumentJoin

@Dao
interface CompanyDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(companyDocumentJoin: CompanyDocumentJoin)

    /*
    * Query documentDetails for single document
    * */

    @Query("SELECT CDJ.company_id, CDJ.document_id, CDJ.verified_at FROM company_document_join AS CDJ INNER JOIN company AS C ON C.id IN (SELECT COM.id FROM company as COM JOIN user as US ON US.id = COM.user_id  WHERE US.account_address =:accountAddress) JOIN document as D ON D.user_id in (SELECT US.id FROM user AS US WHERE US.account_address =:accountAddress) AND C.id=:companyId LIMIT 1")
    fun selectCompanyDocumentJoinLive(accountAddress: String, companyId: String): LiveData<CompanyDocumentJoin>

    @Query("SELECT CDJ.company_id, CDJ.document_id, CDJ.verified_at FROM company_document_join AS CDJ INNER JOIN company AS C ON C.id IN (SELECT COM.id FROM company as COM JOIN user as US ON US.id = COM.user_id  WHERE US.account_address =:accountAddress) JOIN document as D ON D.user_id in (SELECT US.id FROM user AS US WHERE US.account_address =:accountAddress) AND C.id=:companyId LIMIT 1")
    fun selectCompanyDocumentJoin(accountAddress: String, companyId: String): CompanyDocumentJoin

}