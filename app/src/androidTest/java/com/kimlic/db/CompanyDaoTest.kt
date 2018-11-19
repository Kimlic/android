package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.Company
import com.kimlic.db.entity.User
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyDaoTest : DbTest() {

    @Test
    fun companyDaoTest() {
        val companyDao = db.companyDao()

        val userId = db.userDao().insert(User())
        val company = Company(address = "New York", email = "someEmail@gmeil.com", logo = "www.kimlic/logo", id = "1", userId = userId)
        companyDao.insert(company)

        val companyDb = companyDao.company(id = "1")
        assertEquals(companyDb.address, "New York")
    }
}