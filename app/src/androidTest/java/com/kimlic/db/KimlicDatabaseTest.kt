package com.kimlic.db

import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.InstrumentationRegistry
import com.kimlic.db.entity.*
import com.kimlic.utils.AppDoc
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class KimlicDatabaseTest {

    // Constants

    companion object {
        private const val DEFAULT_ACCOUNT_ADDRESS = "account_address"
        private const val DEFAULT_DOCUMENT_TYPE = "PASSPORT"
        private const val DEFAULT_CONTACT_TYPE = "PHONE"
    }

    // Variables

    private var database: KimlicDB? = null
    private var USER_ID: Long? = null
    private var DOCUMET_ID: Long? = null
    private var context: Context? = null

    @Before
    fun setup() {
        context = InstrumentationRegistry.getContext()
        database = Room.inMemoryDatabaseBuilder(context!!, KimlicDB::class.java).build()
    }

    @Test
    fun databaseInstanceNotNull() {
        Assert.assertNotNull(database)
    }

    @Test
    fun testDataBaseWriteReadUser() {
        val user = User(
                firstName = "Jon",
                lastName = "Smith",
                kimQuantity = 1,
                mnemonic = "kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic kimlic",
                portraitFile = "portrait",
                portraitPreviewFile = "portrait_preview",
                accountAddress = DEFAULT_ACCOUNT_ADDRESS)

        USER_ID = database!!.userDao().insert(user)
        assertNotNull(USER_ID)

        val user1 = database!!.userDao().select(accountAddress = DEFAULT_ACCOUNT_ADDRESS)
        val user1Id = user1.id

        Assert.assertEquals(user1.firstName, "Jon")
        Assert.assertEquals(user1.lastName, "Smith")
        Assert.assertEquals(user1Id, USER_ID!!)

        // Documents test

        val document = Document(userId = USER_ID!!, number = "documentNumber", country = "usa", state = "pending", type = DEFAULT_DOCUMENT_TYPE)
        DOCUMET_ID = database!!.documentDao().insert(document)

        val docFromDB = database!!.documentDao().select(accountAddress = DEFAULT_ACCOUNT_ADDRESS, documentType = DEFAULT_DOCUMENT_TYPE)
        assertNotNull(docFromDB)

        // Contact test

        val contact = Contact(userId = USER_ID!!, type = DEFAULT_CONTACT_TYPE, value = "123456789", approved = true)
        database!!.contactDao().insert(contact)

        val contactsFromDB = database!!.contactDao().select(accountAddress = DEFAULT_ACCOUNT_ADDRESS)
        assertEquals(contactsFromDB.size, 1)

        // Company test

        val company = Company(address = "New York", name = "Kimlic", website = "www.demo.kimlic.com", userId = USER_ID!!)
        database!!.companyDao().insert(company)

        val companyFromDB = database!!.companyDao().companies(accountAddress = DEFAULT_ACCOUNT_ADDRESS)
        assertNotNull(companyFromDB)

        // Photo test

        val photo = Photo(documentId = DOCUMET_ID, userId = USER_ID, synced = true, type = AppDoc.PASSPORT.type)
        database!!.photoDao().insert(listOf(photo))

        val photoFromDB = database!!.photoDao().selectUserPhotosByDocument(accountAddress = DEFAULT_ACCOUNT_ADDRESS, documentType = AppDoc.PASSPORT.type)
        assertNotNull(photoFromDB)


        database!!.userDao().delete(USER_ID!!)

        val nullUser = database!!.userDao().select(USER_ID!!)
        val nullDoc = database!!.documentDao().select(accountAddress = DEFAULT_ACCOUNT_ADDRESS, documentType = DEFAULT_DOCUMENT_TYPE)
        val nullContact = database!!.documentDao().select(accountAddress = DEFAULT_ACCOUNT_ADDRESS)
        val nullCompany = database!!.companyDao().companies(accountAddress = DEFAULT_ACCOUNT_ADDRESS)
        val nullPhoto = database!!.photoDao().selectUserPhotosByDocument(accountAddress = DEFAULT_ACCOUNT_ADDRESS, documentType = AppDoc.PASSPORT.type)

        assertNull(nullUser)
        assertNull(nullDoc)
        assertTrue(nullContact.isEmpty())
        assertTrue(nullCompany.isEmpty())
        assertTrue(nullPhoto.isEmpty())
    }


    @After
    fun cleanResources() {
        database = null
        context = null
    }
}