package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.Photo
import com.kimlic.db.entity.User
import com.kimlic.utils.AppDoc
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest : DbTest() {

    @Test
    fun photoDaoTest() {
        val userId = db.userDao().insert(User(accountAddress = "ether_account_address"))
        val documentId = db.documentDao().insert(Document(userId = userId, type = AppDoc.PASSPORT.type))

        val photo = Photo(userId = userId, file = "photo_file", documentId = documentId)
        db.photoDao().insert(photo)

        val photosDb = db.photoDao().selectUserPhotosByDocument(accountAddress = "ether_account_address", documentType = AppDoc.PASSPORT.type)
        assertTrue(!photosDb.isEmpty())
        assertEquals(photosDb.first().file, "photo_file")
    }
}