package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.Company
import com.kimlic.db.entity.CompanyDocumentJoin
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyDocumentDaoTest : DbTest() {

    @Test
    fun companyDocumentDaoTest() {
        val companyId = "1"

        val userId = db.userDao().insert(User(accountAddress = "ether_account_address"))

        db.companyDao().insert(company = Company(address = "New York", email = "someEmail@gmeil.com", logo = "www.kimlic/logo", id = companyId, userId = userId))

        val documentId = db.documentDao().insert(document = Document(userId = userId))

        db.companyDocumentDao().insert(companyDocumentJoin = CompanyDocumentJoin(companyId = companyId, documentId = documentId))

        val companyDocumentJoinDB = db.companyDocumentDao().selectCompanyDocumentJoin(accountAddress = "ether_account_address", companyId = companyId)
        assertNotNull(companyDocumentJoinDB)
    }
}