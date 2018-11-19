package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.utils.AppDoc
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DocumentDaoTest : DbTest() {

    @Test
    fun documentDaoTest() {
        val documentDao = db.documentDao()
        val userId = db.userDao().insert(User(accountAddress = "ether_account_address"))
        val document = Document(userId = userId, number = "AA-123-BB", type = AppDoc.PASSPORT.type)
        documentDao.insert(document)

        val documentDb = documentDao.select(accountAddress = "ether_account_address", documentType = AppDoc.PASSPORT.type)
        assertEquals(documentDb?.number, "AA-123-BB")
    }
}