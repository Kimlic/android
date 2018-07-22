package com.kimlic.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kimlic.KimlicApp
import com.kimlic.db.dao.UserDao
import com.kimlic.db.entity.Address
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User

@Database(entities = arrayOf(User::class, Contact::class, Document::class, Address::class), version = 1)
abstract class KimlicDB : RoomDatabase() {

    abstract fun userDao1(): UserDao

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