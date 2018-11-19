package com.kimlic.db

import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.entity.VendorDocument
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VendorDaoTest : DbTest() {

    @Test
    fun vendorDaoTest() {
        val vendorDocument0 = VendorDocument(contexts = listOf("AA", "BB", "CC", "DD"), countries = listOf("UA", "EN", "TR"))
        val vendorDocument1 = VendorDocument(contexts = listOf("EE", "DD", "EE", "HH"), countries = listOf("US", "FR", "GE"))

        db.vendorDao().insertDocs(listOf(vendorDocument0, vendorDocument1))

        val vendorDocsFromDB = db.vendorDao().select()
        assertTrue(vendorDocsFromDB.isNotEmpty())
        db.vendorDao().deleteAll()
        val nullVendorsDocs = db.vendorDao().select()
        assertTrue(nullVendorsDocs.isEmpty())
    }
}