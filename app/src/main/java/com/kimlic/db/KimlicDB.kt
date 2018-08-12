package com.kimlic.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kimlic.KimlicApp
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*

@Database(entities = arrayOf(User::class, Contact::class, Document::class, Address::class, Photo::class, VendorDocument::class), version = 1)

abstract class KimlicDB : RoomDatabase() {

    // Variables

    private val TAG = this::class.java.simpleName

    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun contactDao(): ContactDao
    abstract fun documentDao(): DocumentDao
    abstract fun photoDao(): PhotoDao
    abstract fun vendorDao(): VendorDao

    companion object {
        private val TAG = this::class.java.simpleName
        private var INSTANCE: KimlicDB? = null

        fun getInstance(context: Context? = KimlicApp.applicationContext()): KimlicDB? {
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


}