package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.User
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactDaoTest : DbTest() {

    @Test
    fun contactDaoTest() {
        val userId = db.userDao().insert(User())

        db.contactDao().insert(Contact(userId = userId, type = "phone", value = "+123456789"))

        val contactDb = db.contactDao().selectByUserIdAndType(userId = userId, type = "phone")
        assertEquals(contactDb.value, "+123456789")
    }
}