package com.kimlic.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.kimlic.KimlicApp
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*

@Database(entities = [User::class, Company::class, Contact::class, Document::class, Address::class, Photo::class, VendorDocument::class, CompanyDocumentJoin::class], version = 1, exportSchema = false)

abstract class KimlicDB : RoomDatabase() {

    // Companion

    companion object {
        private var INSTANCE: KimlicDB? = null

        fun getInstance(): KimlicDB? {
            if (INSTANCE == null) {
                synchronized(KimlicDB::class) {
                    INSTANCE = Room.databaseBuilder(KimlicApp.applicationContext(), KimlicDB::class.java, "kimlic.db")
                            .allowMainThreadQueries()
                            .build()

                    INSTANCE!!.openHelper.setWriteAheadLoggingEnabled(false)
                    INSTANCE!!.invalidationTracker
                }
            }
            return INSTANCE
        }
    }

    // Dao

    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun contactDao(): ContactDao
    abstract fun documentDao(): DocumentDao
    abstract fun photoDao(): PhotoDao
    abstract fun vendorDao(): VendorDao
    abstract fun companyDao(): CompanyDao
    abstract fun companyDocumentDao(): CompanyDocumentDao
}