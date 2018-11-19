package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.User
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest : DbTest() {

    @Test
    fun userDaoTest() {
        val user = User(firstName = "John", lastName = "Doe")
        val id = db.userDao().insert(user)
        val userDb = db.userDao().select(id = id)

        assertEquals(userDb.firstName, "John")
        assertEquals(userDb.lastName, "Doe")
    }
}