package com.kimlic.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kimlic.KimlicApp
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*

@Database(entities = arrayOf(User::class, Contact::class, Document::class, Address::class, Photo::class), version = 1)
abstract class KimlicDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun documentDao(): DocumentDao
    abstract fun addressDao(): AddressDao
    abstract fun contactDao(): ContactDao
    abstract fun photoDao(): PhotoDao

    companion object {
        private var INSTANCE: KimlicDB? = null

        fun getInstance(context: Context? = KimlicApp.applicationContext()): KimlicDB? {
            if (INSTANCE == null) {
                synchronized(KimlicDB::class) {
                    INSTANCE = Room.databaseBuilder(KimlicApp.applicationContext(), KimlicDB::class.java, "kimlic.db").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstanse() {
            INSTANCE = null
        }
    }
}