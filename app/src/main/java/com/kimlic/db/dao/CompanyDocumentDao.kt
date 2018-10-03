package com.kimlic.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kimlic.db.entity.CompanyDocumentJoin

@Dao
interface CompanyDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(companyDocumentJoin: CompanyDocumentJoin)

    @Query("SELECT CDJ.document_id, CDJ.company_id, CDJ.verified_at FROM company_document_join as CDJ JOIN user as U WHERE company_id=:companyId AND document_id=:documentId")
    fun selectCompanyJoin(companyId: String, documentId: Long): CompanyDocumentJoin

}